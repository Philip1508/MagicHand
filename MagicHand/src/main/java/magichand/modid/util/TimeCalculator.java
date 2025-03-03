package magichand.modid.util;

public abstract class TimeCalculator {

    public static final int tickSecond = 20;
    public static final Rational tickRationalSecond = new Rational(20,1);

    public static int secondsToTick(int seconds)
    {
        return tickSecond * seconds;
    }

    public static int minutesToTick(int minutes)
    {
        return secondsToTick((minutes * 60));
    }

    public static int secondsToTick(Rational seconds)
    {
        Rational r = seconds.multiply(tickRationalSecond);

        return r.getWholeAndFlatten();

    }
    public static int minutesToTick(Rational minutes)
    {
        return secondsToTick(minutes.multiply(new Rational(60,1)));
    }



    public static void main(String[] argv)
    {

        System.out.println(secondsToTick(5));
        System.out.println(secondsToTick(new Rational(5,1)));

        System.out.println(minutesToTick(8));
        System.out.println(minutesToTick(new Rational(125,10)));



    }

}
