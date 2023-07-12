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

object NaiveFibo {
  def fibonacci(n: Int): Int =
    n match {
      case 0 => 0
      case 1 => 1
      case _ => fibonacci(n - 1) + fibonacci(n - 2)
    }
}

object OptiFibo {
  def fibonacci(n: Int): Int = {
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

object OptiFunctionalFibo {
  def fibo_rec(upto: Int, prev: Int, current: Int): Int = upto match {
    case -1 => 0
    case 0  => current
    case _  => fibo_rec(upto - 1, current, prev + current)
  }
  def fibonacci(n: Int): Int = fibo_rec(n - 1, 0, 1)
}

object MemoizedFibo {
  var computed = scala.collection.mutable.Map(0 -> 0, 1 -> 1)
  def fibonacci(n: Int): Int = {
    if (computed.contains(n)) {
      computed(n)
    } else {
      val res = fibonacci(n - 1) + fibonacci(n - 2)
      computed += (n -> res)
      res
    }
  }
}

object Main extends App {
  val nbs = 0 until 13
  for (i <- nbs) println(Months.months_to_string(i))
  println(NaiveFibo.fibonacci(10))
  println(OptiFibo.fibonacci(10))
  println(MemoizedFibo.fibonacci(10))
  println(OptiFunctionalFibo.fibonacci(10))
}
