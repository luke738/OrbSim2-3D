import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * SystemSimulator handles the simulation for a group of Bodies.
 * It is responsible for updating their position and velocity through numerical linear multistep integration.
 */
public class SystemSimulator
{
    /**Bodies are stored in this ArrayList; this is how the simulator keeps track of them.
     * Any number of bodies can be added.*/
    public static List<Body> bodies = new ArrayList<Body>();
    /**The universal gravitational constant.*/
    public static double G = 6.673*Math.pow(10,-11);
    /**A conversion factor from pixels to meter; each pixel is equal to this number of meters.*/
    public static double spaceCompression = 1000000000;
    /**A conversion factor from each frame in the simulation to seconds; each frame is equal to this number of seconds*/
    public static double timeCompression = 86400/4;
    /**Keeps track of how many frames have passed; used to pick an integration method and for timekeeping*/
    private int stepCount = 0;
    public static double maxMass;
    public static int initNumBodies;

    /**Accepts no parameters, populates bodies with contents of populateSystem().*/
    public SystemSimulator()
    {
        populateSystem();
    }

    /**Creates the Body objects and adds them to the bodies ArrayList.
     * Once the simulation is launched, bodies can only be removed (through collision)*/
    public void populateSystem()
    {
        int maxI = 1;
        Random rand = new Random(37);
        for (int i = 0; i < maxI*16; i++)
        {
            for (int j = 0; j < maxI*9; j++)
            {
                double r1 = rand.nextDouble();
                double r2 = rand.nextDouble();
                double r3 = rand.nextDouble();
                double r4 = rand.nextDouble();
                double r5 = rand.nextDouble();
                double r6 = rand.nextDouble();
                double r7 = rand.nextDouble();
                double r8 = rand.nextDouble();
                double r9 = rand.nextDouble();
                bodies.add(new Body(Integer.toString(i)+"."+Integer.toString(j), 2*((r1+1)/2)*Math.pow(10, 27), 1000000000*((r2+1)/2), new Vector3D(r3*SystemAnimator.B_WIDTH*SystemSimulator.spaceCompression, r4*SystemAnimator.B_HEIGHT*SystemSimulator.spaceCompression, r7*SystemAnimator.B_DEPTH*SystemSimulator.spaceCompression), new Vector3D((r5*2-1)*1000, (r6*2-1)*1000, (r8*2-1)*1000), new int[]{0, 0, (int)(255*r9)}));
            }
        }
        /*bodies.add(new Body("Sun", 1.988*Math.pow(10, 30), 6.955*Math.pow(10, 8), new Vector3D(50*11 * SystemSimulator.spaceCompression, 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,0,0), new int[]{255, 255, 128}));
        bodies.add(new Body("Mercury", 3.301*Math.pow(10, 23), 2.439*Math.pow(10, 6), new Vector3D(50*11*SystemSimulator.spaceCompression+6.389*Math.pow(10,10), 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,44150,0), new int[]{255, 200, 0}));
        bodies.add(new Body("Venus", 4.867*Math.pow(10, 24), 6.051*Math.pow(10, 6), new Vector3D(50*11*SystemSimulator.spaceCompression+1.0828*Math.pow(10,11), 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,-34990,0), new int[]{255, 128, 0}));
        bodies.add(new Body("Earth", 5.972*Math.pow(10, 24), 6.367*Math.pow(10, 6), new Vector3D(50*11*SystemSimulator.spaceCompression+1.496*Math.pow(10,11), 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,0,29800), new int[]{0, 255, 255}));
        bodies.add(new Body("Mars", 6.4169*Math.pow(10, 23), 3.396*Math.pow(10, 6), new Vector3D(50*11*SystemSimulator.spaceCompression+2.1138*Math.pow(10,11), 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,18349.420971790908258201911196621,18349.420971790908258201911196621), new int[]{255, 0, 0}));*/
        //bodies.add(new Body("Test1", Math.pow(10,30), 5*Math.pow(10,8), new Vector3D(50*10 * SystemSimulator.spaceCompression, 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,0,0), new int[]{255, 0, 0}));
        //bodies.add(new Body("Test2", Math.pow(10,30), 5*Math.pow(10,8), new Vector3D(50*12 * SystemSimulator.spaceCompression, 50*7 * SystemSimulator.spaceCompression, 0), new Vector3D(0,0,0), new int[]{0, 0, 255}));
        colorDimension();
        System.out.println(bodies.size());
        initNumBodies = bodies.size();
    }

    public void colorDimension()
    {
        bodies.sort(new Comparator<Body>() {
            @Override
            public int compare(Body o1, Body o2) {
                if (o1.size>o2.size)
                {
                    return 1;
                }
                else if (o2.size>o1.size)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }
        });
        maxMass=bodies.get(0).mass;
        for (Body body : bodies)
        {
            body.color[0]=(int)(255D/bodies.size())*(bodies.indexOf(body)+1);
        }
    }

    /**Updates the velocity and position of each Body each frame.
     * The preferred method of integration is the 3rd order Adams-Bashford method, but this requires information from 3 timesteps in the past.
     * As this is not available at the start of the simulation, it first makes use of the Euler method for a frame.
     * It then uses the two sets of position-velocity to use a more accurate 2nd order method.
     * At this point, the 3rd frame in the simulation, it has enough data to use the 3rd order method and does so for the rest of the simulation.
     * This system allows for roughly a 4-fold increase in speed with no loss of accuracy, or a 4-fold increase in accuracy with no loss of speed
     * (compared to using solely the Euler method).*/
    public void updateSystem()
    {
        List<Body> copyBodies = new ArrayList<Body>(bodies);
        //Simul computation loop
        for (Body body : bodies)
        {
            if(!body.doRemove)
            {
                if (stepCount == 0)
                {
                    eulerStep(body);
                }
                else if (stepCount == 1)
                {
                    adamBash2ndOrdStep(body);
                }
                else
                {
                    adamBash3rdOrdStep(body);
                }
                //body.collisionChk();
                //System.out.println(body.ident+" "+body.velocity.toString());
            }
            else
            {
                copyBodies.remove(body);
            }
        }
        bodies = copyBodies;
        //Insert simul-update loop here.
        for(Body body : bodies)
        {
            body.velocity=body.velNew;
            body.location=body.locNew;
        }
        for (Body body : bodies)
        {
            body.collisionChk();
        }
        stepCount++;
        if(stepCount%(365*4)==0)
        {
            System.out.println("Year: "+stepCount/(365*4));
        }
    }

    public void draw(Graphics2D g2)
    {
        List<Body> copyBodies = new ArrayList<Body>(bodies);
        copyBodies.sort(new Comparator<Body>() {
            @Override
            public int compare(Body o1, Body o2) {
                if (o1.location.z > o2.location.z) {
                    return 1;
                } else if (o2.location.z > o1.location.z) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (Body body : copyBodies)
        {
            body.draw(g2);
        }
    }

    /**Updates a bodies position-velocity through the Euler method, a 1st order linear explicit method for numerical integration.
     * Requires no more information than the current position and velocity, but stores the last values of them before simulating for future use.
     * Least accurate of the three implemented methods, but requires little information, used only for the first frame.
     * @param body The body to be simulated.*/
    public void eulerStep(Body body)
    {
        body.accelOld=body.acceleration();
        body.velOld=body.velocity;
        body.velNew=body.velocity.add(body.acceleration().scalarMult(timeCompression));
        //System.out.println(body.acceleration().x+" "+body.acceleration().y);
        body.locNew=body.location.add(body.velocity.scalarMult(timeCompression));
    }

    /**Updates a bodies position-velocity through the 2nd order explicit Adams-Bashford method.
     * Requires both the current information and information from 1 frame ago, but stores info from 2 frames ago.
     * Intermediate accuracy, intermediate information needs, used only for the second frame.
     * @param body The body to be simulated.*/
    public void adamBash2ndOrdStep(Body body)
    {
        body.accel2Old=body.accelOld;
        body.accelOld=body.acceleration();

        body.vel2Old=body.velOld;
        body.velOld=body.velocity;
        body.velNew=body.velocity.add(body.accelOld.scalarMult(1.5*timeCompression)).sub(body.accel2Old.scalarMult(0.5*timeCompression));

        body.locNew=body.location.add(body.velOld.scalarMult(1.5*timeCompression).sub(body.vel2Old.scalarMult(0.5*timeCompression)));
    }

    public void adamBash3rdOrdStep(Body body)
    {
        body.accel3Old = body.accel2Old;
        body.accel2Old = body.accelOld;
        body.accelOld = body.acceleration();

        body.vel3Old=body.vel2Old;
        body.vel2Old=body.velOld;
        body.velOld=body.velocity;
        body.velNew=body.velocity.add(body.accelOld.scalarMult(timeCompression*(23D/12D))).sub(body.accel2Old.scalarMult(timeCompression*(4D/3D))).add(body.accel3Old.scalarMult(timeCompression*(5D/12D)));

        body.locNew=body.location.add(body.velOld.scalarMult(timeCompression*(23D/12D))).sub(body.vel2Old.scalarMult(timeCompression*(4D/3D))).add(body.vel3Old.scalarMult(timeCompression*(5D/12D)));
    }
}
