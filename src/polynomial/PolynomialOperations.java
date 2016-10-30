package polynomial;

/** Provides utility methods for performing common operations on polynomials.
* The usual include addition, subtraction, multiplication, division, derivation etc.
* WARNING: The methods in this class are not thread-safe.
*
* @author Murzea Radu
*/
public class PolynomialOperations
{
	//storage for the polynomials on which the operations are performed
	private Polynomial x, y;

	//place-holder for the PolynomialOperations object.
	//part of the singleton pattern implementation
	private static PolynomialOperations _instance;
	
	/** Constructs a PolynomialOperations object. Only 1 instance of this class can be created.
	* Subsequent calls of this method will do nothing.
	*
	* @param x the first polynomial
	*
	* @param y the second polynomial
	*
	* @throws NullPointerException if any of the parameters are null.
	*/
	public static PolynomialOperations createOperationsObject (Polynomial x, Polynomial y)
	{
		//if the instance was not created, do it now
		if (_instance == null)
		{
			_instance = new PolynomialOperations (x, y);
		}
		
		return _instance;
	}

	//Constructs a PolynomialOperations object.
	//parameters: the 2 polynomials
	private PolynomialOperations (Polynomial x, Polynomial y)
	{
		//check if any of the polynomials are null
		if (x == null || y == null)
		{
			//complain if they are
			throw new NullPointerException ("null polynomial provided");
		}

		//store the polynomials as fields
		this.x = x;
		this.y = y;
	}

	/** Swaps the 2 polynomials (useful for the division operation for example).
	*/
	public final void swap ()
	{
		Polynomial aux = x;
		x = y;
		y = aux;
	}
	
	/** Changes the first polynomial of the operations to the one specified.
	* 
	* @param newpol the new Polynomial object.
	*
	* @throws NullPointerException if the parameter is null.
	*/
	public final void setFirstPolynomial (Polynomial newpol)
	{
		if (newpol == null)
		{
			throw new NullPointerException ("null polynomial provided");
		}
 
		x = newpol;
	}
	
	/** Changes the second polynomial of the operations to the one specified.
	* 
	* @param newpol the new Polynomial object.
	*
	* @throws NullPointerException if the parameter is null.
	*/
	public final void setSecondPolynomial (Polynomial newpol)
	{
		if (newpol == null)
		{
			throw new NullPointerException ("null polynomial provided");
		}

		y = newpol;
	}
	
	/** Checks if the Polynomials associated with this object can be raised to a specific power. A Polynomial
	* is allowed to be raised to a specific power only if the resulting Polynomial has all magnitudes
	* smaller than 1000. This method should always be called before trying to raise a Polynomial to a power.
	*
	* @param polynomial the polynomial for which the check should be made.
	*
	* @param power the power for which the check should be made.
	*
	* @return true if the operation is allowed, false otherwise.
	* 
	* @throws IllegalArgumentException if the polynomial parameter is not 1 or 2.
	*/
	public final boolean isPowerAllowed (int polynomial, int power)
	{
		if (polynomial != 1 && polynomial != 2)
		{
			throw new IllegalArgumentException ("invalid value of parameter polynomial");
		}
		else if (polynomial == 1)
		{
			return (isPowerAllowed (x, power));
		}
		else if (polynomial == 2)
		{
			return (isPowerAllowed (y, power));
		}

		return false;
	}
	
	/** Checks if a Polynomial can be raised to a specific power. A Polynomial is allowed to be raised
	* to a specific power only if that power is non-negative and the resulting Polynomial has all
	* magnitudes smaller than 1000.
	* This method should always be called before trying to raise a Polynomial to a power.
	*
	* @param p the polynomial for which the check should be made.
	*
	* @param power the power for which the check should be made.
	*
	* @return true if the operation is allowed, false otherwise.
	* 
	* @throws NullPointerException if the parameter p is null.
	*/
	public static boolean isPowerAllowed (Polynomial p, int power)
	{
		if (p == null)
		{
			throw new NullPointerException ("null polynomial provided.");
		}
		
		return ((p.getBiggestNonZeroIndex () + 1) * power <= 999 && power >= 0);
	}

	/** Performs the addition operation on the 2 polynomials. This method respects every mathematical
	* condition for this operation (like commutativity or associativity or neutral element etc.).
	* 
	* @return a Polynomial which represents the sum of the 2 Polynomials provided.
	*/
	public final Polynomial add ()
	{
		return add (x, y);
	}

	/** Performs the addition operation on the 2 polynomials provided as parameters. This method
	* respects every mathematical condition for this operation (like commutativity or associativity
	* or neutral element etc.).
	*
	* @param p1 the first polynomial
	* 
	* @param p2 the second polynomial
	* 
	* @throws NullPointerException if any of the 2 parameters is null.
	* 
	* @return a Polynomial which represents the sum of the 2 Polynomials provided.
	*/
	public static Polynomial add (Polynomial p1, Polynomial p2)
	{
		if (p1 == null || p2 == null)
		{
			throw new NullPointerException ("null provided where Polynomial was expected");
		}
		
		int i, sz1 = p1.getBiggestNonZeroIndex () + 1,
			   sz2 = p2.getBiggestNonZeroIndex () + 1;

		Polynomial result;

		//check the sizes of the 2 polynomials (size of result <<== size of biggest of them)
		if (sz2 > sz1)
		{
			//create the result
			result = new Polynomial (sz2);

			//add the coefficients that exist for both polynomials
			for (i = 0; i < sz1; ++i)
			{
				result.setElement (i, p2.getElement (i) + p1.getElement (i));
			}

			//add coefficients that exist only in the biggest polynomial
			for (i = sz1; i < sz2; ++i)
			{
				result.setElement (i, p2.getElement (i));
			}
		}
		else
		{
			//create the result
			result = new Polynomial (sz1);

			//add the coefficients that exist for both polynomials
			for (i = 0; i < sz2; ++i)
			{
				result.setElement (i, p2.getElement (i) + p1.getElement (i));
			}

			//add coefficients that exist only in the biggest polynomial
			for (i = sz2; i < sz1; ++i)
			{
				result.setElement (i, p1.getElement (i));
			}
		}

		return result;
	}

	/** Performs the subtraction operation on the 2 polynomials provided as parameters.
	*
	* @param p1 the first polynomial
	* 
	* @param p2 the second polynomial
	* 
	* @throws NullPointerException if any of the 2 parameters is null.
	* 
	* @return a Polynomial which represents the difference of the 2 Polynomials provided (p1 minus p2).
	*/
	public static Polynomial subtract (Polynomial p1, Polynomial p2)
	{
		if (p1 == null || p2 == null)
		{
			throw new NullPointerException ("null provided where Polynomial was expected");
		}

		int i, sz1 = p1.getBiggestNonZeroIndex () + 1,
			   sz2 = p2.getBiggestNonZeroIndex () + 1;

		Polynomial result;

		if (sz2 > sz1)
		{
			result = new Polynomial (sz2);

			for (i = 0; i < sz1; ++i)
			{
				result.setElement (i, p1.getElement (i) - p2.getElement (i));
			}

			for (i = sz1; i < sz2; ++i)
			{
				result.setElement (i, 0.0 - p2.getElement (i));
			}
		}
		else
		{
			result = new Polynomial (sz1);

			for (i = 0; i < sz2; ++i)
			{
				result.setElement (i, p1.getElement (i) - p2.getElement (i));
			}

			for (i = sz2; i < sz1; ++i)
			{
				result.setElement (i, p1.getElement (i));
			}
		}

		return result;
	}

	/** Performs the subtraction operation on the 2 polynomials.
	*
	* @return a Polynomial which represents the difference of the 2 Polynomials provided.
	*/
	public final Polynomial subtract ()
	{
		return subtract (x, y);
	}

	/** Multiplies the 2 polynomials.
	 * 
	 * @return the Polynomial that represents the multiplication of the 2 polynomials associated with
	 * this object.
	*/
	public final Polynomial multiply ()
	{	
		return multiply (x, y);
	}
	
	/** Multiplies the 2 polynomials given as parameters.
	* 
	* @param p1 the first Polynomial.
	* 
	* @param p2 the seconds Polynomial.
	* 
	* @throws NullPointerException if any of the 2 parameters is null.
	* 
	* @return the Polynomial that represents the multiplication of the 2 parameters.
	*/
	public static Polynomial multiply (Polynomial p1, Polynomial p2)
	{
		if (p1 == null || p2 == null)
		{
			throw new NullPointerException ("null provided where Polynomial was expected.");
		}
		
		int i, j, sz1 = p1.getBiggestNonZeroIndex (),
				  sz2 = p2.getBiggestNonZeroIndex ();

		//storage for the existing coefficient in "this" polynomial
		double oldvalue;

		//create the result according to the 2 polynomial's sizes
		Polynomial result = new Polynomial (sz1 + sz2 + 1);

		//multiply each coefficient in the 1st polynomial with each coefficient in the 2nd
		for (i = 0; i <= sz1; ++i)
		{
			for (j = 0; j <= sz2; ++j)
			{
				oldvalue = result.getElement (i + j);

				//add the product to the existing value
				result.setElement (i + j, oldvalue + p1.getElement (i) * p2.getElement (j));
			}
		}

		return result;
	}

	/** Derivates one of the polynomials.
	 * 
	 * @param polynomial specifies which polynomial to derivate. Accepted values are 1 and 2.
	 *
	 * @throws IllegalArgumentException if the polynomial parameter is out of range.
	 * 
	 * @return the derivated Polynomial.
	*/
	public final Polynomial derivate (int polynomial)
	{		
		switch (polynomial)
		{
			case 1:
			{
				return derivate (x);
			}
			case 2:
			{
				return derivate (y);
			}
			default:
			{
				throw new IllegalArgumentException ("polynomial parameter is out of range.");
			}
		}
	}

	/** Derivates the existing polynomial and returns it.
	* 
	* @param p The polynomial to be derivated.
	* 
	* @throws NullPointerException if p is null.
	* 
	* @return the derivated Polynomial.
	*/
	public static Polynomial derivate (Polynomial p)
	{
		if (p == null)
		{
			throw new NullPointerException ("null polynomial provided.");
		}

		//create the resulting polynomial
		int result_size = (p.getBiggestNonZeroIndex () > 0) ? p.getBiggestNonZeroIndex () : 1;
		Polynomial result = new Polynomial (result_size);

		//set the coefficient for each magnitude
		for (int i = 0; i < result_size; ++i)
		{
			result.setElement (i, ((double) (i + 1)) * p.getElement (i + 1));
		}

		return result;
	}

	/** Raises the first polynomial to a given power.
	* 
	* @param pow The power to which to raise. If x = 0, a polynomial with the value of 1 is
	* returned. If x = 1, the unmodified polynomial is returned.
	*
	* @throws IllegalArgumentException If the power is negative.
	*/
	public final Polynomial toPower (int polynomial, int pow)
	{
		if (polynomial != 1 && polynomial != 2)
		{
			throw new IllegalArgumentException ("polynomial parameter is out of range.");
		}
		else if (! isPowerAllowed (polynomial, pow))
		{
			throw new IllegalArgumentException ("invalid power. negative or exceeds maximum of 999 result magnitude");
		}
		
		return ((polynomial == 1) ? toPower (x, pow) : toPower (y, pow));
	}

	/** Raises a polynomial to a given power.
	 * 
	* @param p The polynomial to be raised.
	*
	* @param power The power to which to raise.
	*
	* @throws IllegalArgumentException If the power is not allowed for this polynomial. Check with the
	* isPowerAllowed method before calling.
	* 
	* @throws NullPointerException if p is null.
	* 
	* @return the Polynomial p risen to the power x. If x = 0, a Polynomial with the value of 1 is
	* returned. If x = 1, the unmodified Polynomial is returned.
	*/
	public static Polynomial toPower (Polynomial p, int power)
	{
		if (p == null)
		{
			throw new NullPointerException ("null polynomial provided.");
		}

		if (! isPowerAllowed (p, power))
		{
			//cannot raise to a forbidden power
			throw new IllegalArgumentException ("invalid power");
		}
		else if (power == 0)
		{
			//anything to the power 0 equals 1
			Polynomial result = new Polynomial (1);
			result.setElement (0, 1);

			return result;
		}
		else if (power == 1)
		{
			//anything to the power 1 equals themselves
			//so create a copy of the existing polynomial
			Polynomial result = new Polynomial (p.getBiggestNonZeroIndex () + 1);

			for (int i = 0; i <= p.getBiggestNonZeroIndex (); ++i)
			{
				result.setElement (i, p.getElement (i));
			}

			return result;
		}

		Polynomial result = new Polynomial ((p.getBiggestNonZeroIndex () + 1) * power);	//the resulting polynomial

		result.setElement (0, (double) 1);

		//raise to the power
		for (int i = 0; i < power; ++i)
		{
			result = multiply (result, p);
		}

		return result;
	}
	
	/** Divides the first polynomial by the second.
	*
	* @throws ArithmeticException if the second Polynomial has all coefficients equal to zero.
	* 
	* @return an array of Polynomials of size 2. The first element in this array is the result of the
	* division, the second element is the rest. Both elements are non-null.
	*/
	public final Polynomial[] divide ()
	{
		return divide (x, y);
	}
	
	/** Divides the first polynomial by the second.
	* 
	* @param p1 the first Polynomial.
	* 
	* @param p2 the second Polynomial.
	* 
	* @throws ArithmeticException if the second Polynomial has all coefficients equal to zero.
	* 
	* @return an array of Polynomials of size 2. The first element in this array is the result of the
	* division, the second element is the rest. Both elements are non-null.
	*/
	public static Polynomial[] divide (Polynomial p1, Polynomial p2)
	{
		int i;
		
		int xbigg = p1.getBiggestNonZeroIndex (), ybigg = p2.getBiggestNonZeroIndex ();

		Polynomial[] totalresult = new Polynomial[2];

		//divisor has only 1 non zero coefficient
		if (ybigg == 0)
		{
			//divisor can not be zero. error.
			if (p2.isZero ())
			{
				throw new ArithmeticException ("DIVISION BY ZERO ERROR");
			}

			Polynomial result = new Polynomial (xbigg + 1);

			//the result is divident / coefficient
			for (i = 0; i <= xbigg; ++i)
			{
				result.setElement (i, p1.getElement (i) / p2.getElement (0));
			}

			totalresult[0] = result;
			totalresult[1] = null;

			return totalresult;
		}

		if (xbigg < ybigg)
		{
			Polynomial result = new Polynomial (1),
						 rest = new Polynomial (xbigg + 1);

			for (i = 0; i < xbigg + 1; ++i)
			{
				rest.setElement (i, p1.getElement (i));
			}

			totalresult[0] = result;
			totalresult[1] = rest;

			return totalresult;
		}

		Polynomial result = new Polynomial (xbigg - ybigg + 1),
					 rest = new Polynomial (p1.getMaxSize ()),
					  neg = new Polynomial (1),
					 interm,
					 interm2;

		neg.setElement (0, (double) -1);

		for (int j = 0; j < p1.getMaxSize (); ++j)
		{
			rest.setElement (j, p1.getElement (j));
		}

		do
		{
			i = rest.getBiggestNonZeroIndex () - ybigg;

			result.setElement (i, rest.getBiggestNonZeroCoefficient () / p2.getBiggestNonZeroCoefficient ());

			interm2 = new Polynomial (i + 1);
			interm2.setElement (i, result.getElement (i));

			interm = multiply (multiply (interm2, p2), neg);

			rest = add (rest, interm);

		} while (rest.getBiggestNonZeroIndex () >= ybigg);

		totalresult[0] = result;
		totalresult[1] = rest;

		return totalresult;
	}
}