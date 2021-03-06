package com.mrkaspa.ml.knn

import java.io.File

import breeze.linalg._

/**
 * Created by michelperez on 9/16/15.
 */
trait KNN {
  type MDouble = DenseMatrix[Double]
  type VDouble = DenseVector[Double]

  def meassure(s: String)(f: () => Unit): Unit = {
    val timestamp: Long = System.currentTimeMillis
    f()
    val lastTimestamp: Long = System.currentTimeMillis
    println(s"elapsed time in $s")
    println((lastTimestamp - timestamp) / 1000d)
  }

  def classify(input: MDouble, dataSet: MDouble, labels: Array[Double], k: Int): Int = {
    val vec = input.toDenseVector
    val diffMat = dataSet(*, ::) - vec
    var timestamp: Long = System.currentTimeMillis
    val sqDiffMat: MDouble = diffMat :^ 2.0
    val sqDistances: VDouble = sum(sqDiffMat(::, *)).t // sum per row
    val distances: VDouble = sqDistances :^ 0.5 // sqrt
    val sortedDistances: Seq[Int] = argtopk(distances, k) // sort the arguments
    val mapAcc = (0 to (k - 1)).map { i => labels(sortedDistances(i)) }.groupBy(identity).mapValues(_.size)
    val (res, _) = mapAcc.foldLeft((0.0, 0)) { (t, curr) => if (t._2 > curr._2) t else curr }
    res.toInt
  }


  object KNN extends KNN with App {
    val file = new File(getClass.getResource("/datingData.txt").getFile)
    val csv = csvread(file, separator = '\t')
    val mat = csv(::, 0 to 2)
    val labels = csv(::, 3).toArray
    val labelsMap = Map(3 -> "High", 2 -> "Medium", 1 -> "Low")

    val input: MDouble = DenseMatrix(Array(1000.0, 2.0, 5.0))

    val res = classify(input, mat, labels, 5)
    println(s"solucion => ${labelsMap(res)}")

  }

}
