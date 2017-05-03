
/**
 * A simple generator for random numbers.
 * @author Jimmy Collins <jbc1@student.cs.ucc.ie> 
 * Adapted fom a class by Kieren Herley <k.herley@cs.ucc.ie>
 *
 **/

import java.util.Random;

public class RandomNumGenerator
{
 
/**
 *  Create a generator of numbers in range 1 to
 * n inclusive. The numbers are gererated uniformly
 * at random.
 */
public RandomNumGenerator(int n)
{
  randomSource = new Random();
  max = n;
}

/**
 * Return the next random number.
 * @return a number in range 1 to max
 */
public int nextNumber()
{
  return randomSource.nextInt(max) + 1;
}


private Random randomSource;
private int max;

public static void main(String args[])
{
  RandomNumGenerator g = new RandomNumGenerator(10);

  for (int i = 0; i < 20; i++)
  {
    System.out.println(g.nextNumber());
  }
}
}


