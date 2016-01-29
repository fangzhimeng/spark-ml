package linalg.matrix
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix
import org.apache.spark.mllib.linalg.distributed.IndexedRow
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix
import org.apache.spark.mllib.linalg.distributed.MatrixEntry
import org.apache.spark.rdd.RDD

object SparkMatrix {

  def main(args: Array[String]) {
    import org.apache.spark.mllib.linalg.{Matrices, Matrix}

    val dMatrix: Matrix = Matrices.dense(2, 2, Array(1.0, 2.0, 3.0, 4.0))
    println("dMatrix: \n" + dMatrix)

    val sMatrixOne: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 2, 1), Array(5, 6, 7))
    println("sMatrixOne: \n" + sMatrixOne)

    val sMatrixTwo: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 1, 2), Array(5, 6, 7))
    println("sMatrixTwo: \n" + sMatrixTwo)

    val sc = new SparkContext("local[2]", "First Spark App")
    val denseData = Seq(
      Vectors.dense(0.0, 1.0, 2.0),
      Vectors.dense(3.0, 4.0, 5.0),
      Vectors.dense(6.0, 7.0, 8.0),
      Vectors.dense(9.0, 0.0, 1.0)
    )
    val sparseData = Seq(
      Vectors.sparse(3, Seq((1, 1.0), (2, 2.0))),
      Vectors.sparse(3, Seq((0, 3.0), (1, 4.0), (2, 5.0))),
      Vectors.sparse(3, Seq((0, 6.0), (1, 7.0), (2, 8.0))),
      Vectors.sparse(3, Seq((0, 9.0), (2, 1.0)))
    )

    val denseMat = new RowMatrix(sc.parallelize(denseData, 2))
    val sparseMat = new RowMatrix(sc.parallelize(sparseData, 2))

    println("Dense Matrix - Num of Rows :" + denseMat.numRows())
    println("Dense Matrix - Num of Cols:" + denseMat.numCols())
    println("Sparse Matrix - Num of Rows :" + sparseMat.numRows())
    println("Sparse Matrix - Num of Cols:" + sparseMat.numCols())

    val data = Seq(
      (0L, Vectors.dense(0.0, 1.0, 2.0)),
      (1L, Vectors.dense(3.0, 4.0, 5.0)),
      (3L, Vectors.dense(9.0, 0.0, 1.0))
    ).map(x => IndexedRow(x._1, x._2))
    val indexedRows: RDD[IndexedRow] = sc.parallelize(data, 2)
    val indexedRowsMat = new IndexedRowMatrix(indexedRows)
    println("Indexed Row Matrix - No of Rows: " + indexedRowsMat.numRows())
    println("Indexed Row Matrix - No of Cols: " + indexedRowsMat.numCols())

    val entries = sc.parallelize(Seq(
      (0, 0, 1.0),
      (0, 1, 2.0),
      (1, 1, 3.0),
      (1, 2, 4.0),
      (2, 2, 5.0),
      (2, 3, 6.0),
      (3, 0, 7.0),
      (3, 3, 8.0),
      (4, 1, 9.0)), 3).map { case (i, j, value) =>
      MatrixEntry(i, j, value)
    }
    val coordinateMat = new CoordinateMatrix(entries)
    println("Coordinate Matrix - No of Rows: " + coordinateMat.numRows())
    println("Coordinate Matrix - No of Cols: " + coordinateMat.numCols())

    sc.stop()

  }

}
