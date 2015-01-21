/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.mllib.kernels

import breeze.linalg.{DenseVector, norm}
import org.apache.spark.Logging
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Standard RBF Kernel of the form
 * K(Xi,Xj) = exp(-||Xi - Xj||**2/2*bandwidth**2)
 */

class RBFKernel(private var bandwidth: Double)
  extends SVMKernel[RDD[((Int, Int), Double)]] with Logging with Serializable {

  def setBandwidth(d: Double): Unit = {
    this.bandwidth = d
  }

  override def evaluate(x: linalg.Vector, y: linalg.Vector): Double = {
    val diff: linalg.Vector = Vectors.fromBreeze(x.toBreeze - y.toBreeze)
    Math.exp(-1*Math.pow(Vectors.norm(diff, 2.0), 2)/(2*Math.pow(bandwidth, 2)))
  }

  override def buildKernelMatrixasRDD(mappedData: RDD[(Int, LabeledPoint)],
                                      length: Long):
  KernelMatrix[RDD[((Int, Int), Double)]] =
    SVMKernel.buildSVMKernelMatrix(mappedData, length, this.evaluate)

}
