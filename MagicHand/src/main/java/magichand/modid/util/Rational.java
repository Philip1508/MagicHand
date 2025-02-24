package magichand.modid.util;


import net.minecraft.nbt.NbtCompound;

/**
 * This class represents a partial implementation of Rational Numbers, which are used for Mana regeneration.
 *
 * */
public class Rational {


    private int numerator;
    private int denominator;

    private static final String numeratorKey = "numeratorKey";
    private static final String denominatorKey = "denominatorKey";

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
            throwDivByZeroException();
        }
        this.denominator = denominator;
    }




    /**
     * This method adds 2 Rationals together.
     * @param toAdd - Rational to add.
     * @return New Rational that is the sum of the old and new rational.
     */
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


    /**
     * This Method multiplies rationals.
     * @param toMultiply - Rational to multiply with.
     * @return - New Rational that is the multiplication of the old and new rational.
     */
    public Rational multiply(Rational toMultiply)
    {
        return new Rational(this.getNumerator() * toMultiply.getNumerator(), this.getDenominator() * toMultiply.getDenominator());
    }


    /**
     * This method sets the numerator of a rational.
     * @param numerator - New Numerator
     */
    public void setNumerator(int numerator)
    {
        this.numerator = numerator;
    }

    /**
     * This method sets the new denominator of a rational.
     * @param denominator - New denominator. Must NOT be 0.
     */
    public void setDenominator(int denominator)
    {
        if (denominator == 0)
        {
            throwDivByZeroException();
        }
        
        this.denominator = denominator;
    }


    /**
     * This method fetches the numerator of a rational.
     * @return - Primitive Int numerator.
     */
    public int getNumerator()
    {
        return this.numerator;
    }


    /**
     * This method fetches the denominator of a rational.
     * @return - Primitive Int denominator.
     */
    public int getDenominator()
    {
        return this.denominator;
    }


    /**
     * This method calculates wether the Rational is greater or equal to 1.
     * @return - Boolean, true iff Rational >= 1.
     */
    public boolean greaterOne()
    {
        return this.numerator >= this.denominator;
    }

    /**
     * This method gets the amount of whole numbers contained within the rational.
     * (i.e. 24/5 would be 4 + 4/5) and returns the whole amount (4)
     * WARNING: This method "removes" the whole amount from the rational! It's "consumed" by the return value for
     * further calculation! You MUST use the return value of this method!
     * @return - Whole Amount of Rational.
     * ToDo; For the sake of completion, add a version of this method which doesn't consume the whole amount.
     */
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


    /**
     * This method quickly shortens the rational via Bitshifting common 0's at the end.
     * This is a "quick and dirty" approach, which should suffice since the rationals shouldn't live long enough
     * to clutter up too badly. (=> No need for advanced euclidean algorithm).
     */
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


    /**
     * This Method serializes a Rational to a NbtCompound.
     * @param rational - Rational to Serialize.
     * @return - NbtCompbound containing serialized Rational, ready for Networking.
     */
    public static NbtCompound rationalToNbt(Rational rational)
    {
        int numerator = rational.getNumerator();
        int denominator = rational.getDenominator();

        NbtCompound nbt = new NbtCompound();

        nbt.putInt(Rational.numeratorKey, numerator);
        nbt.putInt(Rational.denominatorKey, denominator);

        return nbt;
    }


    /**
     * This method deserializes a Rational from a NbtCompound.
     * ToDo; This may be unsafe: Since primitive ints are used there can be no proper null checking / it unclear.
     * @param nbt - NbtContaining Rational.
     * @return - Rational
     */
    public static Rational rationalFromNbt(NbtCompound nbt)
    {

        int numerator = nbt.getInt(Rational.numeratorKey);
        int denominator = nbt.getInt(Rational.denominatorKey);

        if (denominator == 0)
        {
            throw new IllegalArgumentException("The Denominator in a deserialized Rational is 0. (Division by 0)");
        }

        return new Rational(numerator, denominator);

    }


    /**
     * Old Test function.
     * @param args - None.
     */
    public static void main(String[] args) {

        Rational test = new Rational(8, 16);
        Rational test2 = test.add(new Rational(1,3));

        test = test2;

        test.shorten();

        System.out.println(String.valueOf(test.getNumerator()) + "/" + String.valueOf(test.getDenominator())  );


    }



    private static void throwDivByZeroException() {
        throw new IllegalArgumentException("One cannot divide by zero.");
    }


}
