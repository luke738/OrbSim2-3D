/**
 * A general purpose two-dimensional vector with some methods for manipulation.
 * Encapsulation is not used, but the methods return a new Vector.
 */
public class Vector3D
{
    /**First dimension of the vector*/
    public double x;
    /**Second dimension of the vector*/
    public double y;
    public double z;

    /**Creates a two-dimensional vector
     * @param a Value of first dimension
     * @param b Value of second dimension*/
    public Vector3D(double a, double b, double c)
    {
        x=a;
        y=b;
        z=c;
    }

    /**The magnitude of the vector
     * @return A double value for the length of the vector*/
    public double getMag()
    {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }

    /**Adds another vector to this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the addition*/
    public Vector3D add(Vector3D v2)
    {
        return new Vector3D(x+v2.x,y+v2.y,z+v2.z);
    }

    /**Subtracts another vector from this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the subtraction*/
    public Vector3D sub(Vector3D v2)
    {
        return new Vector3D(x-v2.x,y-v2.y,z-v2.z) ;
    }

    /**Multiplies the vector by a scalar
     * @param scalar A scalar value, a double
     * @return A new Vector resulting from the multiplication*/
    public Vector3D scalarMult(double scalar)
    {
        return new Vector3D(x*scalar, y*scalar, z*scalar);
    }

    public Vector3D average(Vector3D v2)
    {
        return new Vector3D((x+v2.x)/2, (y+v2.y)/2, (z+v2.z)/2);
    }

    public String toString()
    {
        return "X: "+x+"  Y: "+y+"  Z: "+z;
    }
}
