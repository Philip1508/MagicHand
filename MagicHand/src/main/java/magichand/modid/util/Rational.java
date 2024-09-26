package magichand.modid.util;


/**
 * This class represents a partial implementation of Rational Numbers, which are used for Mana regeneration.
 *
 * */
public class Rational {


    private int numerator;
    private int denominator;

    /**
     * This Construtor is used to create a Rational Number which solely represents an integer.
     * This is necessary for calculations which combine "integers" and rationals.
     * @param numerator
     */
    public Rational(int numerator)
    {
        this.numerator = numerator;
        this.denominator = 1;
    }

    /**
     * Constructor for Rational number.
     * Note that it will throw an illegal argument exception on the attempt of dividing by zero.
     * @param numerator
     * @param denominator
     */
    public Rational(int numerator, int denominator)
    {
        this.numerator = numerator;
        if (denominator == 0)
        {
            throw new IllegalArgumentException("One cannot divide by zero.");
        }
        this.denominator = denominator;
    }



    public Rational add(Rational toAdd)
    {
        int numerator_a = this.numerator;
        int numberator_b = toAdd.getNumerator();

        int denominator_a = this.denominator;
        int denominator_b = toAdd.getDenominator();

        if (denominator_a != denominator_b)
        {
            return new Rational(numerator_a * denominator_b
                    + numberator_b * denominator_a,
                    denominator_a * denominator_b);
        }
        else
        {
            return new Rational(this.numerator + toAdd.getNumerator(), denominator_a);
        }

    }


    public Rational multiply(Rational toMultiply)
    {
        return new Rational(this.getNumerator() * toMultiply.getNumerator(), this.getDenominator() * toMultiply.getDenominator());
    }



    public int getNumerator()
    {
        return this.numerator;
    }

    public int getDenominator()
    {
        return this.denominator;
    }


    public boolean greaterOne()
    {
        return this.numerator >= this.denominator;
    }

    public int getWholeAndFlatten()
    {
        int i = 0;
        while (numerator >= denominator)
        {
            numerator -= denominator;
            i += 1;
        }
        return i;

    }


    public void shorten()
    {
        if (numerator >= 0 || denominator >= 0)
        {
            return;
        }
        while ((numerator & 1) == 0 && (denominator & 1) == 0)
        {
            numerator = numerator >> 1;
            denominator = denominator >> 1;
        }
    }





    public static void main(String[] args) {




        Rational test = new Rational(8, 16);
        Rational test2 = test.add(new Rational(1,3));

        test = test2;

        test.shorten();

        System.out.println(String.valueOf(test.getNumerator()) + "/" + String.valueOf(test.getDenominator())  );


    }



}
