import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A gravitationally interacting body, with methods to compute its current acceleration
 * based on the presence of other bodies.
 */
public class Body
{
    /**Name of the body; a body only interacts gravitationally with other bodies with unique names*/
    public String ident;
    /**Mass in kilograms*/
    public double mass;
    /**Radius in meters*/
    public double size;
    /**Density in kg/m^3, computed when object is instantiated*/
    public double density;
    /**x-y position of the body in the simulation, origin at top left*/
    public Vector3D location;
    /**Velocity of the body*/
    public Vector3D velocity;
    /**An RGB color for visual identification*/
    int[] color;
    /**Acceleration 1 timestep in the past*/
    public Vector3D accelOld;
    /**Acceleration 2 timesteps in the past*/
    public Vector3D accel2Old;
    /**Acceleration 3 timesteps in the past*/
    public Vector3D accel3Old;
    /**Velocity 1 timestep in the past*/
    public Vector3D velOld;
    /**Velocity 2 timesteps in the past*/
    public Vector3D vel2Old;
    /**Velocity 3 timesteps in the past*/
    public Vector3D vel3Old;
    public Boolean doRemove = false;

    /*Simultaneous simulation variables*/
    public Vector3D locNew;
    public Vector3D velNew;

    /**Creates a body with all required information to run the simulation.
     * @param id Name of the Body
     * @param m Mass of the Body
     * @param r Radius of the Body
     * @param loc Initial location of the Body
     * @param vel Initial velocity of the Body
     * @param col An RGB color to identify the Body*/
    public Body(String id, double m, double r, Vector3D loc, Vector3D vel, int[] col)
    {
        ident=id;
        mass=m;
        size=r;
        density=m/((4D/3D)*Math.PI*Math.pow(r, 3));
        location = loc;
        velocity = vel;
        color = col;
    }

    /**Computes current acceleration of the body due to the gravitational influence of other bodies in their current positions.
     * @return A Vector corresponding to acceleration along the x and y axis*/
    public Vector3D acceleration()
    {
        Vector3D totalAcceleration = new Vector3D(0, 0, 0);
        for(Body b : SystemSimulator.bodies)
        {
            //System.out.println(b.doRemove);
            if (!ident.equals(b.ident) && !doRemove && !b.doRemove)
            {
                totalAcceleration=totalAcceleration.add(twoBodyAcceleration(b));
            }
        }
        //System.out.println("ta "+totalAcceleration);
        return totalAcceleration;
    }

    /**Used by acceleration(), finds gravity due to one other body.
     * @param b2 The other Body
     * @return Acceleration towards that Body along the x and y axis*/
    public Vector3D twoBodyAcceleration(Body b2)
    {
        Vector3D displacement = location.sub(b2.location);
        double distance = displacement.getMag();
        //System.out.println(distance);
        double undirectedAccel = -SystemSimulator.G*(b2.mass/Math.pow(distance,2));
        //System.out.println(undirectedAccel);
        return displacement.scalarMult(undirectedAccel).scalarMult(1/distance);
    }

    public void collisionChk()
    {
        for(Body body : SystemSimulator.bodies)
        {
            if(!body.ident.equals(ident) && !body.doRemove && !doRemove)
            {
                Vector3D positionVector = body.location.sub(location);
                //System.out.println(positionVector.getMag());
                //System.out.println("s "+size+body.size);
                if (positionVector.getMag() < size + body.size)
                {
                    //System.out.println(ident+" "+velocity.toString());
                    //System.out.println("m1 "+velocity.scalarMult(mass));
                    //System.out.println("m2 "+body.velocity.scalarMult(body.mass));
                    Vector3D momentum = velocity.scalarMult(mass).add(body.velocity.scalarMult(body.mass));
                    mass+=body.mass;
                    double massFrac = body.mass/mass;
                    //body.mass=0;
                    double vol = mass / (body.density*massFrac+density*(1-massFrac));
                    //double oVol = body.mass / body.density;
                    size = Math.cbrt((3D / 4D) * vol / Math.PI);
                    double relativeSize = size/SystemSimulator.maxSize;
                    System.out.println(relativeSize);
                    if (relativeSize<=1)
                    {
                        color = new int[]{(int)(relativeSize*255), 0, (int) (color[2] * (1 - massFrac) + body.color[2] * (massFrac))};
                    }
                    else
                    {
                        System.out.println("g "+(int)((255D/SystemSimulator.initNumBodies)*(int)(relativeSize)));
                        color = new int[]{(int)(255*(relativeSize/Math.ceil(relativeSize))), (int)((255D/SystemSimulator.initNumBodies)*(int)(relativeSize)), (int) (color[2] * (1 - massFrac) + body.color[2] * (massFrac))};
                    }
                    location=new Vector3D(location.x*(1-massFrac)+body.location.x*massFrac, location.y*(1-massFrac)+body.location.y*(massFrac), location.z*(1-massFrac)+body.location.z*(massFrac));
                    //System.out.println("m "+momentum);
                    //System.out.println(1/mass);
                    //System.out.println("v "+momentum.scalarMult(1/mass));
                    setAllVel(momentum.scalarMult(1 / mass));
                    body.doRemove=true;
                    setAllAccel();
                    if(velocity.getMag()>1500)
                    {
                        System.out.println("WARN: "+ident);
                        System.out.println(momentum);
                    }
                    //System.out.println(ident+" "+mass);
                    //body.size = Math.cbrt((3D / 4D) * oVol / Math.PI);
                    //velocity=new Vector(0,0);
                    //SystemSimulator.bodies.remove(body);
                }
            }
        }
    }

    public void setAllVel(Vector3D newVel)
    {
        velocity=newVel;
        velOld=newVel;
        vel2Old=newVel;
        vel3Old=newVel;
    }

    public void setAllAccel()
    {
        //System.out.println(acceleration());
        accelOld=acceleration();
        accel2Old=accelOld;
        accel3Old=accel2Old;
    }

    /**Draws the body to the GUI*/
    public void draw(Graphics2D g2)
    {
        g2.setColor(new Color(color[0], color[1], color[2]));
        double zSize = 5*Math.log10(10+location.z/SystemSimulator.spaceCompression);
        if(location.z<0)
        {
            zSize = 10-5*Math.log10(10-location.z/SystemSimulator.spaceCompression);
        }
        if(zSize<1)
        {
            zSize = 1;
        }
        //System.out.println(zSize);
        g2.fill(new Ellipse2D.Double((location.x)/SystemSimulator.spaceCompression-zSize, (location.y)/SystemSimulator.spaceCompression-zSize, (zSize*2), (zSize*2)));
    }
}

