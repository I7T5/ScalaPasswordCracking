import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global  // Ina: Future wants an execution context, whatever that means...

// Instructor Example Times
// Sequential: Found 83 passwords of length 4 in 921.411 seconds
// Parallel:   Found 83 passwords of length 4 in 151.388 seconds (8-core CPU)

@main def crackPasswords(): Unit = {
  // load password hashes from the starter pack
  val hashes: Set[String] = getHashes
  // define some character sets to test against the hashes
  val digits = "0123456789"
  val lowercase = "abcdefghijklmnopqrstuvwxyz"
  val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  val symbols = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
  val fullCharset = lowercase + uppercase + digits + symbols


  var length = 3

  // bruteCollection
  var timeItResult: (Double, Iterable[String]) = timeIt(bruteForceCollection(fullCharset, length, hashes))
  var seconds: Double = timeItResult(0) / 1000
  var passwords: Iterable[String] = timeItResult(1)
  println(s"bruteCollection: Found ${passwords.size} passwords of length $length in $seconds seconds! ")

//  // bruteLoop
//  timeItResult = timeIt(bruteForceLoop(fullCharset, length, hashes))
//  println(s"bruteLoop: Found ${timeItResult(1).size} passwords of length $length in ${timeItResult(0) / 1000} seconds! ")

  // bruteFutureCollection
  timeItResult = timeIt(bruteForceFutureCollection(fullCharset, length, hashes))
  seconds = timeItResult(0) / 1000
  passwords = timeItResult(1)
  println(s"futureCollection: Found ${passwords.size} passwords of length $length in $seconds seconds! ")

  // bruteFutureLoop
  var timeItResultWithUnit = timeIt(bruteForceFutureLoop(fullCharset, length, hashes))
  seconds = timeItResultWithUnit(0) / 1000
  var numPasswords: BigInt = timeItResultWithUnit(1)
  println(s"futureLoop: Found ${numPasswords} passwords of length $length in $seconds seconds! ")


//  // try brute forcing all passwords of a specific length n
//  val length = 1
//  val passwords = bruteForceCollection(fullCharset, length, hashes)
//  println(s"One Character Passwords: ${passwords.mkString(", ")}")
//  // note that the getCombination function could also be used to combine words . . .
//  val words = Vector("correct", "horse", "battery", "staple")
//  println(getCombination(words)(2)(BigInt(7)).mkString)
}

def bruteForceFutureCollection(charset: String, length: Int, hashes: Set[String]): Iterable[String] = {
  // identify the range of BigInt that correspond to possible passwords
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  // partially apply getCombination to get a simpler function
  val makeCombination = getCombination(charset)(length)
  // use collection methods on a range of BigInts to find possible passwords and filter for ones with matching hashes
  (start until stop).view
      .map((x: BigInt) => Future(makeCombination(x).mkString))
      .map((fComb: Future[String]) => Await.result(fComb, Duration.Inf))  // just trying to fit Future in somewhere...
      .filter((pwd: String) => hashes contains sha256(pwd))
}


def bruteForceFutureLoop(charset: String, length: Int, hashes: Set[String]): BigInt = {
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  // create a partially applied version of get combination with symbols and length filled in
  val makeCombination = getCombination(charset)(length)
  // loop from the first possible password with this charset to the last checking each
  var cursor = start

  var numPasswords: BigInt = BigInt(0) // I know mutability is dangerous but hopefully this works...
  def printPasswords(index: BigInt): Unit = {
    // identify the password that goes with this particular number
    val password = makeCombination(index).mkString
    // if this password's hash is in the set, add the password to the output list of passwords
    if (hashes contains sha256(password)) {
      numPasswords += 1
//      print(s"$password, ")
    }
  }

  // Construct a queue line for await...The hope is that the futures made first will be the first to be done
  // kinda like a sliding window I guess
//  println(stop)
  val futures = mutable.Queue[Future[Unit]]()
  val futuresSize = if stop < Int.MaxValue then stop.intValue else Int.MaxValue
  for i <- 0 until futuresSize do futures.enqueue(Future(printPasswords(i)))

  cursor = futuresSize
  while futures.nonEmpty do {
    Await.result(futures.dequeue, Duration.Inf)
    if cursor < stop then futures.enqueue(Future(printPasswords(cursor)))
    cursor += 1
  }
  println()

  numPasswords
}


// The following are examples provided by Dr. Dickinson

// brute force try every order of chars in charset with replacement using collection methods
def bruteForceCollection(charset: String, length: Int, hashes: Set[String]): Iterable[String] = {
  // identify the range of BigInt that correspond to possible passwords
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  // partially apply getCombination to get a simpler function
  val makeCombination = getCombination(charset)(length)
  // use collection methods on a range of BigInts to find possible passwords and filter for ones with matching hashes
  (start until stop).view
    .map((x: BigInt) => makeCombination(x).mkString)
    .filter((pwd: String) => hashes contains sha256(pwd))
}

// brute force try every order of chars in charset with replacement using a for loop
// NOTE: this one might be an easier starting point for futures and doesn't have issues with Int.MaxValue
def bruteForceLoop(charset: String, length: Int, hashes: Set[String]): Iterable[String] = {
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  // create a partially applied version of get combination with symbols and length filled in
  val makeCombination = getCombination(charset)(length)
  // create a buffer to hold passwords that have been identified
  val passwords = mutable.ArrayBuffer[String]()
  // loop from the first possible password with this charset to the last checking each
  var cursor = start
  while cursor < stop do {
    // identify the password that goes with this particular number
    val password = makeCombination(cursor).mkString
    // if this password's hash is in the set, add the password to the output list of passwords
    if hashes contains sha256(password) then passwords.addOne(password)
    // increment the cursor
    cursor += 1
  }
  // convert the buffer to an immutable vector and return it
  passwords.toVector
}

// compute the SHA-256 hash of the string using Java's message digest tools
def sha256(text: String): String = {
  MessageDigest.getInstance("SHA-256")
    .digest(text.getBytes("UTF-8"))
    .map("%02x".format(_)).mkString.toUpperCase
}

/**
 * Converts a BigInt into a number in base symbols.size where symbols are the digits.
 * These conversions are restricted by length (number of digits) and include the
 * equivalent of leading zeros to better match the application.
 *
 * @param symbols - the symbols in this base (IndexedSeq for efficient index lookup)
 * @param length -  the number of 'digits' in the output symbol sequence
 * @param index - the number to be converted into a set of symbols in base symbols.size
 * @tparam A    - the type of the symbols (often but not necessarily characters)
 * @return      - a sequence of length possibly repeating items from the symbols unique by index
 */
def getCombination[A](symbols: IndexedSeq[A])(length: Int)(index: BigInt): Seq[A] = {
  // check that the input is in range
  if index < BigInt(0) || index >= BigInt(symbols.size).pow(length) then {
    throw new IndexOutOfBoundsException("Symbol sequence index out of range")
  }
  // create a number buffer to track what symbol comes next
  var numBuffer: BigInt = new BigInt(index.bigInteger)
  // create an output array of items of type A
  val out = mutable.ArrayBuffer[A]()
  // fill the output based on the values of the buffer
  for i <- 0 until length do {
    // interpret the lowest portion of the number as the next symbol
    val next = symbols(numBuffer.mod(symbols.size).toInt)
    out.addOne(next)
    // remove that portion with integer division
    numBuffer /= symbols.size
  }
  // return the updated output buffer as a vector
  out.toVector
}

/**
 * Returns both the answer and the clock time required to compute it for any expression
 *
 * @param f the expression to be executed
 * @tparam A the type to which f evaluates
 * @return the time to compute and value of f
 */
def timeIt[A](f: => A): (Double, A) = {
  val startTime = System.currentTimeMillis()
  val result = f
  val endTime = System.currentTimeMillis()
  (endTime-startTime, result)
}

// loads a set of password hashes from the provided hashes.txt file
def getHashes: Set[String] = {
  val src = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream("hashes.txt"))
  try {
    src.getLines().toSet
  } finally {
    src.close()
  }
}
