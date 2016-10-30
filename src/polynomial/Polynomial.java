package polynomial;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/** Implements a polynomial with all its functionality.
* You can create a polynomial, modify its coefficients and perform
* operations on it by using the PolynomialOperations class.
* 
* @author Murzea Radu.
*/
public final class Polynomial implements Serializable, Comparable<Polynomial>
{
	//coefficients of the polynomial will be stored in this array (index will represent the magnitude)
	private double[] elements;

	//storage for the polynomial's size
	private int polynomialsize;

	//cache storage for the biggest non zero index and coefficient.
	//this information is needed a lot, so why not cache it ? :)
	private int biggest_non_zero_index;
	private double biggest_non_zero_coeff;

	/** The serial version ID for this current implementation of this class (used by the Serializable interface).
	*/
	private static final long serialVersionUID = -8803174995935847264L;

	/** Creates a polynomial of the specified size.
	*
	* @param size The maximum size (magnitude) of the polynomial. Available magnitudes are then between 0 and size - 1.
	*
	* @throws IllegalArgumentException If the size provided is less than 1.
	*/
	public Polynomial (int size)
	{
		//construct the polynomial only if the size is at least 1
		if (size <= 0)
		{
			//complain about the size
			throw new IllegalArgumentException ("invalid polynomial size");
		}
		
		//allocate space for the coefficients
		this.elements = new double[size];

		//store the size
		this.polynomialsize = size;
		
		//since no coefficients yet exist, both cache variables are zero
		this.biggest_non_zero_coeff = 0.0;
		this.biggest_non_zero_index = 0;
		
		Arrays.fill (elements, 0.0);
	}
	
	/** Returns the maximum magnitude of this polynomial.
	*
	* @return the maximum magnitude.
	*/
	public int getMaxSize ()
	{
		return this.polynomialsize;
	}

	/** Stores the coefficient of the polynomial for the magnitude specified.
	*
	* @param magnitude The magnitude for which to set the coefficient.
	*
	* @param value The value of the coefficient.
	*
	* @throws IllegalArgumentException If the polynomial does not contain that magnitude.
	*/
	public void setElement (int magnitude, double value)
	{
		//set the coefficient only if the magnitude exists
		if (magnitude < this.polynomialsize && magnitude >= 0)
		{
			//store the coefficient
			this.elements[magnitude] = value;
			
			//since an element has been change, the biggest non zero index and coefficient
			//need to be recalculated
			calculateBiggestNonZeroIndex ();
			calculateBiggestNonZeroCoeff ();
		}
		else
		{
			//complain about the magnitude
			throw new IllegalArgumentException ("polynomial does not contain that magnitude");
		}
	}

	/** Returns the coefficient stored at the specified magnitude.
	*
	* @param magnitude The magnitude from which to return the coefficient
	*
	* @return the coefficient at the specified magnitude.
	*
	* @throws IllegalArgumentException If the magnitude does not exist for this polynomial.
	*/
	public double getElement (int magnitude)
	{
		//check if the magnitude exists
		if (magnitude < this.polynomialsize && magnitude >= 0)
		{
			//return coefficient
			return this.elements[magnitude];
		}
		else
		{
			//complain about the magnitude
			throw new IllegalArgumentException ("element with that magnitude does not exist.");
		}
	}

	/** Returns the value of the polynomial for a given value of its variable.
	*
	* @param value The value of the variable. Accepted values are... pretty much anything.
	*
	* @return the value of the polynomial for the given value of its variable.
	*/
	public double getPolynomialValue (double value)
	{
		//we start at 0
		double result = 0;

		for (int i = 0; i < this.polynomialsize; ++i)
		{
			//given the fact that i can get big, let's call
			//Math.pow as rarely as possible. If polynomialsize is big enough, then
			//every call to this method would actually return NaN.
			if (this.elements[i] != (double) 0)
			{
				//for each magnitude, multiply the coefficient with the variable's value to the
				//power of the magnitude
				result += (this.elements[i] * Math.pow (value, i));
			}
		}

		return result;
	}
	
	//calculates the biggest non zero index of this polynomial and stores the result
	private void calculateBiggestNonZeroIndex ()
	{
		//start from the biggest magnitude
		int i = this.polynomialsize - 1;

		//as long as the coefficients are 0, keep going
		while (this.elements[i] == (double) 0)
		{
			//if we're not at the end yet, keep going
			if (i > 0)
			{
				i--;
			}
			//if the end is reached, stop and return the result
			else
			{
				break;
			}
		}

		this.biggest_non_zero_index = i;
	}
	
	//calculates the biggest non zero coefficient of this polynomial and stores the result
	private void calculateBiggestNonZeroCoeff ()
	{
		this.biggest_non_zero_coeff = this.elements[this.biggest_non_zero_index];
	}

	/** Returns the coefficient of the biggest non-zero magnitude.
	* For example: we have a polynomial of size 6 that equals 2x^3 + 5x^0. The call of this method
	* will return 2 (<u>2</u>x^3).
	* 
	* @return the biggest non zero coefficient of this Polynomial
	*/
	public double getBiggestNonZeroCoefficient ()
	{
		return this.biggest_non_zero_coeff;
	}

	/** Returns the index of the biggest non-zero magnitude.
	* For example: we have a polynomial of size 6 that equals 2x^3 + 5x^0. The call of this method
	* will return 3 (2x^<u>3</u>).
	* 
	* @return the magnitude on which the biggest non zero coefficient lies
	*/
	public int getBiggestNonZeroIndex ()
	{
		return this.biggest_non_zero_index;
	}

	/** Checks if this polynomial is zero or not.
	*
	* @return true if all coefficients are zero, false otherwise.
	*/
	public boolean isZero ()
	{
		return (this.biggest_non_zero_index == 0 && this.biggest_non_zero_coeff == (double) 0);
	}

	/** Resets all the polynomials coefficients to zero. */
	public void reset ()
	{
		//set each coefficient to zero
		for (int i = 0; i < this.polynomialsize; i++)
		{
			this.elements[i] = (double) 0;
		}
		
		//reset cache information to zero
		this.biggest_non_zero_coeff = 0.0;
		this.biggest_non_zero_index = 0;
	}
	
	/** Compares two Polynomial objects numerically. Returns a negative integer, zero or a positive
	* integer as this object is less than, equal to or greater than the specified object.
	* Note: this class has a natural ordering that is consistent with equals, BUT only for Polynomial
	* objects that have the same maximum size. To be more precise: this method will ignore the maximum size
	* property of the Polynomials, while the equals method won't.
	*
	* @param p the Polynomial to be compared.
	*
	* @return the value 0 if this object is numerically equal to p, a negative integer if this object
	* is numerically less than p or a positive integer if this object is numerically greater than p.
	*
	* @throws NullPointerException if the parameter p is null.
	*/ 
	@Override public int compareTo (Polynomial p)
	{
		//check if the object exists
		if (p == null)
		{
			throw new NullPointerException ("null pointer given to compareTo method");
		}
		//if the same object is provided as parameter, then comparison is useless
		else if (p == this)
		{
			return 0;
		}

		//start from the biggest non-zero coefficient
		int thisbindex = this.biggest_non_zero_index;
		int pbindex = p.getBiggestNonZeroIndex ();

		//if that is different, the objects are not equal
		if (pbindex != thisbindex)
		{
			return thisbindex - pbindex;
		}

		int coeffdiff;
		
		//traverse the polynomial (doesn't matter from where we start, both indexes are equal)
		for (int i = thisbindex; i >= 0; --i)
		{
			//compare each coefficient of the same magnitude
			coeffdiff = Double.compare (this.elements[i], p.getElement (i));
			
			//if it's different, return a positive/negative number (but NOT zero)
			if (coeffdiff != 0)
			{
				return coeffdiff;
			}
		}

		//if everything passes, the objects are equal
		return 0;
	}

	/** Returns a hash code for this polynomial.
	*
	* @return the hash code of this polynomial. Value will be between 0 and 999.
	*/
	@Override public int hashCode ()
	{
		double hash = 7;

		for (double nr : this.elements)
		{
			if (Double.compare (nr, (double) 0) != 0)
			{
				hash = 17 * hash + nr;
			}
		}

		hash += this.polynomialsize;

		return ((int) (hash % 1000));
	}

	/** Checks if this object is equal to the object provided.
	* Two Polynomial objects are equal if they have the same maximum size and all their coefficients are
	* equal for the same magnitude. If you need a comparison that ignores the maximum size, look at
	* the compareTo method.
	*
	* @param o the Polynomial to be compared.
	*
	* @return true if this Polynomial is equal to the one provided, false otherwise.
	*/
	@Override public boolean equals (Object o)
	{
		//check for null
		if (o == null)
		{
			return false;
		}
		
		//if the same polynomial is given as parameter, equality check is useless
		if (o == this)
		{
			return true;
		}

		//check class of object
		//since Polynomial is final, instanceof can be safely used here
		if (! (o instanceof Polynomial))
		{
			return false;
		}

		//previous check passed, so casting is now safe
		Polynomial p = (Polynomial) o;

		//check for size
		if (this.polynomialsize != p.getMaxSize ())
		{
			return false;
		}

		//compare every coefficient
		for (int i = 0; i < this.polynomialsize; i++)
		{
			if (Double.compare (this.elements[i], p.getElement (i)) != 0)
			{
				return false;
			}
		}

		//objects are equal
		return true;
	}

	/** Returns a string representation of this polynomial.
	*
	* @return a String representation of this polynomial.
	*/
	@Override public String toString ()
	{
		String r = "P(x) = ";

		if (isZero ())
		{
			r += "0";
			return r;
		}

		int i = this.biggest_non_zero_index;

		//format each coefficient to a maximum of 3 fractional digits
		DecimalFormat df = new DecimalFormat ();
		df.setMaximumFractionDigits (3);

		//bla bla something ... it formats it as a string, leave me alone..
		
		if (i > 0)
		{ 
			if (elements[i] == 1.0)
			{
				r += "x^" + Integer.toString (i);
			}
			else if (elements[i] == -1.0)
			{
				r += "- x^" + Integer.toString (i);
			}
			else
			{
				if (elements[i] < 0.0)
				{
					r += "- ";
				}

				r += df.format (Math.abs (elements[i])).toString () + "x^" + Integer.toString (i);
			}
		}

		for (i--; i > 0; --i)
		{
			if (elements[i] != 0.0)
			{
				if (elements[i] == 1.0)
				{
					r += " + ";
				}
				else if (elements[i] == -1.0)
				{
					r += " - ";
				}
				else
				{
					if (elements[i] < 0.0)
					{
						r += " - ";
					}
					else
					{
						r += " + ";
					}

					r += df.format (Math.abs (elements[i])).toString ();
				}

				r += "x^" + Integer.toString (i);
			}
		}

		if (elements[0] != 0.0)
		{
			if (elements[0] == 1.0)
			{
				r += " + 1";
			}
			else if (elements[0] == -1.0)
			{
				r += " - 1";
			}
			else
			{
				if (elements[0] < 0.0)
				{
					r += " - ";
				}
				else if (elements[0] > 0.0 && this.biggest_non_zero_index != 0)
				{
					r += " + ";
				}

				r += df.format (Math.abs (elements[0])).toString ();
			}
		}

		return r;
	}
}