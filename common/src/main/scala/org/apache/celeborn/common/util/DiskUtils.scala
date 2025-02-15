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

package org.apache.celeborn.common.util

import java.nio.file.{Files, Paths}

import org.apache.celeborn.common.meta.DiskInfo

/**
 * Disk utilities provide detail of disk info including disk statistics etc.
 */
object DiskUtils {

  /**
   * Get the actual size that the disk needs to reserve. It will take the larger value between
   * the configured fixed reserved size and the size calculated by the reserved ratio.
   *
   * @param diskInfo The disk info being calculated.
   * @param diskReserveSize The configured fixed reserved size space for the disk.
   * @param diskReserveRatio The configured reserved ratio for the disk.
   * @return the actual size (in bytes) that the disk needs to reserve.
   */
  def getActualReserveSize(
      diskInfo: DiskInfo,
      diskReserveSize: Long,
      diskReserveRatio: Option[Double]): Long = {
    var actualReserveSize = diskReserveSize
    if (diskReserveRatio.isDefined) {
      try {
        val totalSpace = Files.getFileStore(Paths.get(diskInfo.mountPoint)).getTotalSpace
        actualReserveSize =
          BigDecimal(totalSpace * diskReserveRatio.get).longValue.max(actualReserveSize)
      } catch {
        case _: Exception => // Do nothing
      }
    }
    actualReserveSize
  }
}
