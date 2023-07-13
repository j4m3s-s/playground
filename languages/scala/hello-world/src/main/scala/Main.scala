object Month extends Enumeration {
  val January, February, March, April, May, June, July, August, September, October, November, December = Value
}

object Months {
  val values = Month.values.map(_.toString).toList
  val nb_to_months = (1 to values.length).zip(values).toMap
  def months_to_string(n: Int): String = nb_to_months.get(n) match {
    case None => "Error"
    case Some(m) => m
  }
}

trait Fibonacci {
  def apply(n: Int): Int
}

object NaiveFibo extends Fibonacci {
  override def apply(n: Int): Int =
    n match {
      case 0 => 0
      case 1 => 1
      case _ => apply(n - 1) + apply(n - 2)
    }
}

object OptiFibo extends Fibonacci {
  override def apply(n: Int): Int = {
    var i = n - 1
    var current = 1
    var prev = 0

    while (i > 0) {
      val next = current + prev
      prev = current
      current = next

      i -= 1
    }
    current
  }
}

object OptiFunctionalFibo extends Fibonacci {
  def _apply(upto: Int, prev: Int, current: Int): Int = upto match {
    case -1 => 0
    case 0  => current
    case _  => _apply(upto - 1, current, prev + current)
  }
  override def apply(n: Int): Int = _apply(n - 1, 0, 1)
}

object MemoizedFibo extends Fibonacci {
  private var computed = scala.collection.mutable.Map(0 -> 0, 1 -> 1)
  override def apply(n: Int): Int = {
    if (computed.contains(n)) {
      computed(n)
    } else {
      val res = apply(n - 1) + apply(n - 2)
      computed += (n -> res)
      res
    }
  }
}

trait Factorial {
  def apply(n: Int): Int
}

object RecursiveFactorial extends Factorial {
  override def apply(n: Int): Int = n match {
    case 0 => 1
    case _ => n * apply(n - 1)
  }
}

object IterativeFactorial extends Factorial {
  def _run(acc: Int, n: Int): Int = n match {
    case 0 => acc
    case _ => _run(acc * n, n - 1)
  }
  override def apply(n: Int): Int = _run(1, n)
}

// String reversal

trait StringReversal {
  def apply(s: String): String
}

object IterativeStringReversal extends StringReversal {
  override def apply(s: String): String = {
    var res = new StringBuilder()
    for (i <- s.length() - 1 to 0 by -1) {
      res.append(s.charAt(i))
    }
    res.toString()
  }
}

object RecursiveStringReversal extends StringReversal {
  override def apply(s: String): String = s.length() match {
    case 0 => ""
    case _ => s.charAt(s.length() - 1) + apply(s.substring(0, s.length() - 1))
  }
}

// Higher Order Function
object Adder {
  def apply(x: Int)(y: Int): Int = x + y
}

object Main extends App {
  val nbs = 0 until 13
  for (i <- nbs) println(Months.months_to_string(i))
  val fns = List(
    NaiveFibo,
    OptiFibo,
    MemoizedFibo,
    OptiFunctionalFibo
  )

  for (fn <- fns) println(fn(10))

  val factorials = List(
    RecursiveFactorial,
    IterativeFactorial
  )
  for (fn <- factorials) println(fn(10))

  val strReversal = List(
    IterativeStringReversal,
    RecursiveStringReversal
  )
  for (fn <- strReversal) println(fn("Test."))

  println(Adder(5)(10))
}
