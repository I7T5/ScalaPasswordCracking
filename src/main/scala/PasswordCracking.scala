import java.security.MessageDigest
import java.time.LocalDateTime
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

  // TODO: refer to comments for description of each function call if the print statement is different...
  var length = 4

  // bruteCollection
//  var timeItResult = timeIt(bruteForceCollection(fullCharset, length, hashes))
//  var seconds: Double = timeItResult(0) / 1000
//  var passwords: Iterable[String] = timeItResult(1)
//  println(s"bruteCollection: Found ${passwords.size} passwords of length $length in $seconds seconds! ")

//  // bruteLoop
//  timeItResult = timeIt(bruteForceLoop(fullCharset, length, hashes))
//  println(s"bruteLoop: Found ${timeItResult(1).size} passwords of length $length in ${timeItResult(0) / 1000} seconds! ")

  // bruteFutureLoop
//  var timeItResultWithInt = timeIt(bruteForceFutureLoop(fullCharset, length, hashes))
//  var seconds = timeItResultWithInt(0) / 1000
//  var numPasswords = timeItResultWithInt(1)
//  println(s"futureLoop: Found $numPasswords passwords of length $length in $seconds seconds! ")

  // common passwords using bruteForceFutureLoop
//  var timeItResultWithInt = timeIt(bruteForceFutureLoop(getCommonPasswords.toVector, 1, hashes))
//  var seconds = timeItResultWithInt(0) / 1000
//  var numPasswords = timeItResultWithInt(1)
//  println(s"futureLoop: Found $numPasswords passwords in common passwords list in $seconds seconds! ")

  // w/o symbols
//  for length <- 6 to 7 do {
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(lowercase + uppercase + digits, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"futureLoop: Found $numPasswords letter-with-digits passwords of length $length in $seconds seconds! ")
//  }

  // Digits
//  for length <- 6 to 8 do {
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(digits, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"futureLoop: Found $numPasswords numeric passwords of length $length in $seconds seconds! ")
//  }

  // Lowercase + digits
//  for length <- 6 to 6 do {
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(lowercase+digits, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"futureLoop: Found $numPasswords lowercase-with-digits passwords of length $length in $seconds seconds! ")
//  }

//  // common words with varying caps
//  for length <- 1 to 2 do {
//    val words = simpleVaryCaps(getCommonWords)
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(words.toVector, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"Found $numPasswords passwords in $length-common-words-combination with varying caps in $seconds seconds! ")
//  }

//  // common words with varying caps with single suffix
//  for length <- 1 to 2 do {
//    val words = simpleVaryCaps(getCommonWords)
//    var timeItResultWithInt = timeIt(bruteForceFutureLoopWithSuffix(words.toVector, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"Found $numPasswords passwords in $length-common-words-combination with varying caps and 1-char suffix in $seconds seconds! ")
//  }

  // common words with varying caps with 2-char suffix
  for length <- 1 to 1 do {
    val words = simpleVaryCaps(getCommonWords)
    var timeItResultWithInt = timeIt(bruteForceFutureLoopWithSuffix(words.toVector, length, hashes, 2))
    var seconds = timeItResultWithInt(0) / 1000
    var numPasswords = timeItResultWithInt(1)
    println(s"Found $numPasswords passwords in $length-common-words-combination with varying caps and 1-char suffix in $seconds seconds! ")
  }
  
  // common words all lowercase
  for length <- 3 to 3 do {
    val words = getCommonWords
    var timeItResultWithInt = timeIt(bruteForceFutureLoop(words.toVector, length, hashes))
    var seconds = timeItResultWithInt(0) / 1000
    var numPasswords = timeItResultWithInt(1)
    println(s"Found $numPasswords passwords in $length-common-words-combination all lowercase in $seconds seconds! ")
  }

//  // common words all lowercase with 1-char suffix
//  for length <- 3 to 3 do {
//    val words = getCommonWords
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(words.toVector, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"Found $numPasswords passwords in $length-common-words-combination with 1-char suffix in $seconds seconds! ")
//  }






//  for length <- 9 to 9 do {
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(digits, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"futureLoop: Found $numPasswords numeric passwords of length $length in $seconds seconds! ")
//  }
//
//  for length <- 5 until 10 do {
//    var timeItResultWithInt = timeIt(bruteForceFutureLoop(uppercase, length, hashes))
//    var seconds = timeItResultWithInt(0) / 1000
//    var numPasswords = timeItResultWithInt(1)
//    println(s"futureLoop: Found $numPasswords lowercase-letter passwords of length $length in $seconds seconds! ")
//  }


//  // try brute forcing all passwords of a specific length n
//  val length = 1
//  val passwords = bruteForceCollection(fullCharset, length, hashes)
//  println(s"One Character Passwords: ${passwords.mkString(", ")}")
//  // note that the getCombination function could also be used to combine words . . .
//  val words = Vector("correct", "horse", "battery", "staple")
//  println(getCombination(words)(2)(BigInt(7)).mkString)
}

// Helper methods
// loads a set of password hashes from the provided hashes.txt file
def getCommonWords: Set[String] = {
  val src = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream("common-words-4000.txt"))
  try {
    src.getLines().toSet
  } finally {
    src.close()
  }
}

def getCommonPasswords: Set[String] = {
  val src = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream("common-passwords-1000000.txt"))
  try {
    src.getLines().toSet
  } finally {
    src.close()
  }
}

def simpleVaryCaps(words: Set[String]): Set[String] = words.flatMap((word: String) => Set(word.toLowerCase, word.capitalize))


// ONE FUNCTION TO RULE THEM ALL !!!
def bruteForceFutureLoop[A](charset: IndexedSeq[A], length: Int, hashes: Set[String], suffixLength: Int = 0): Int = {
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  val makeCombination = getCombination(charset)(length)

  // Let the number of Futures be the number of cores on your computer's CPU
  // Dr. Dickinson said you generally want it that way so the threads don't waste time shifting from one task to the next
  val numCores = Runtime.getRuntime.availableProcessors

  println(s"Tasked started at " + LocalDateTime.now())
  print("Passwords: ")
  // Divide combinations into numCores parts/Futures using striping (slidedeck #11)
  val futures = for i <- 0 until numCores yield Future {
    // keep variables local to the Futures they're associated with
    var numPasswords = 0
    var cursor = i
    while cursor < stop do {
      val password = makeCombination(cursor).mkString
      if hashes contains sha256(password) then {
        print(s"$password,")
        //println(password)
        numPasswords += 1
      }
      cursor += numCores
    }
    numPasswords
  }

  val totalNumPasswords = futures.map((future: Future[Int]) => Await.result(future, Duration.Inf)).sum
  println()
  totalNumPasswords
}





def commonWords(charset: String, length: Int, hashes: Set[String]): Int = {
  // 1. Find a collection of common words in English. Let that be our charset.
  // 2. Try getCombination(words)(numWords)(BigInt(7)).mkString with numWords = 1 or numWords = 2
  //
  0
}

def commonWordsWithVaryingCaps(charset: String, length: Int, hashes: Set[String]): Int = {
  // 1. Find a collection of common words in English.
  // 2. For each word, map it to a set containing both itself lowercased and itself 1st-char-capitalized.
  //    Let the (flattened) collection of maps be our charset
  // 3. Try getCombination(words)(numWords)(BigInt(7)).mkString with numWords = 1 or numWords = 2...
  // ...
  0
}

// if we want to be the group that finds the most passwords!
def bruteForceFutureLoopWithSuffix[A](charset: IndexedSeq[A], length: Int, hashes: Set[String], suffixLength: Int = 1): Int = {
  // Same as commonWordsWithVaryingCaps except add digits (potentially all combinations of the digits) to charset
  val suffixCharset = "0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
  val makeSuffixCombination: BigInt => Seq[Char] = getCombination(suffixCharset)(suffixLength)
  val suffixStart = BigInt(0)
  val suffixStop = BigInt(suffixCharset.length).pow(suffixLength)

  // bruteForceFutureLoop
  val start = BigInt(0)
  val stop = BigInt(charset.length).pow(length)
  val makeCombination = getCombination(charset)(length)

  // Let the number of Futures be the number of cores on your computer's CPU
  // Dr. Dickinson said you generally want it that way so the threads don't waste time shifting from one task to the next
  val numCores = Runtime.getRuntime.availableProcessors

  println(s"Tasked started at " + LocalDateTime.now())
  print("Passwords: ")
  // Divide combinations into numCores parts/Futures using striping (slidedeck #11)
  val futures = for i <- 0 until numCores yield Future {
    // keep variables local to the Futures they're associated with
    var numPasswords = 0
    var cursor = i
    while cursor < stop do {
      val password = makeCombination(cursor).mkString
      for j <- suffixStart until suffixStop do {
        val passwordWithSuffix = password + makeSuffixCombination(j).mkString
        if hashes contains sha256(passwordWithSuffix) then {
          print(s"$passwordWithSuffix ")
          //println(password)
          numPasswords += 1
        }
      }
      cursor += numCores
    }
    numPasswords
  }

  val totalNumPasswords = futures.map((future: Future[Int]) => Await.result(future, Duration.Inf)).sum
  println()
  totalNumPasswords
}

/**
 *
 * @param charset Vector[words]
 * @param length number of words per password
 * @param hashes hashes to check with
 * @return
 */
def bruteForceFutureLoopWithTwoCharSuffix(charset: String, length: Int, hashes: Set[String]): Int = {
  // Same as commonWordsWithVaryingCaps except add  symbols to charset
  val tempComb: Int => BigInt => Seq[Char] = getCombination("0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~")

  val makeUnicharSuffix = tempComb(1)
  val makeBicharSuffix = tempComb(2)


  0
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
