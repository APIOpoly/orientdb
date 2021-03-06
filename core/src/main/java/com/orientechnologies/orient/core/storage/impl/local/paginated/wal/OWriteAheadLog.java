/*
 *
 *  *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 *
 */

package com.orientechnologies.orient.core.storage.impl.local.paginated.wal;

import com.orientechnologies.orient.core.storage.impl.local.OCheckpointRequestListener;
import com.orientechnologies.orient.core.storage.impl.local.OLowDiskSpaceListener;
import com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OAtomicOperationMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 6/25/14
 */
public interface OWriteAheadLog {
  OLogSequenceNumber logFuzzyCheckPointStart(OLogSequenceNumber flushedLsn) throws IOException;

  OLogSequenceNumber logFuzzyCheckPointEnd() throws IOException;

  OLogSequenceNumber logFullCheckpointStart() throws IOException;

  OLogSequenceNumber logFullCheckpointEnd() throws IOException;

  OLogSequenceNumber getLastCheckpoint();

  OLogSequenceNumber begin() throws IOException;

  OLogSequenceNumber begin(long segmentId) throws IOException;

  OLogSequenceNumber end();

  void flush() throws IOException;

  OLogSequenceNumber logAtomicOperationStartRecord(boolean isRollbackSupported, OOperationUnitId unitId) throws IOException;

  OLogSequenceNumber logAtomicOperationEndRecord(OOperationUnitId operationUnitId, boolean rollback, OLogSequenceNumber startLsn,
      Map<String, OAtomicOperationMetadata<?>> atomicOperationMetadata) throws IOException;

  OLogSequenceNumber log(OWALRecord record) throws IOException;

  void truncate() throws IOException;

  void close() throws IOException;

  void close(boolean flush) throws IOException;

  void delete() throws IOException;

  void delete(boolean flush) throws IOException;

  OWALRecord read(OLogSequenceNumber lsn) throws IOException;

  OLogSequenceNumber next(OLogSequenceNumber lsn) throws IOException;

  OLogSequenceNumber getFlushedLsn();

  void cutTill(OLogSequenceNumber lsn) throws IOException;

  void cutAllSegmentsSmallerThan(long segmentId) throws IOException;

  void addFullCheckpointListener(OCheckpointRequestListener listener);

  void removeFullCheckpointListener(OCheckpointRequestListener listener);

  void addLowDiskSpaceListener(OLowDiskSpaceListener listener);

  void removeLowDiskSpaceListener(OLowDiskSpaceListener listener);

  void moveLsnAfter(OLogSequenceNumber lsn) throws IOException;

  void preventCutTill(OLogSequenceNumber lsn) throws IOException;

  File[] nonActiveSegments(long fromSegment);

  long[] nonActiveSegments();

  long activeSegment();

  void newSegment() throws IOException;

  /**
   * Adds the event to fire when this write ahead log instances reaches the given LSN.
   * <p>
   * The thread on which the event will be fired is unspecified, the event may be even fired synchronously before this method
   * returns. Avoid running long tasks in the event handler since this may degrade the performance of this write ahead log and/or
   * its event managing component.
   * <p>
   * The exact LSN, up to which this write ahead log is actually grown, may differ from the event's LSN at the moment of invocation.
   * But it's guarantied that the write ahead log's LSN will be larger than or equal to the event's LSN. In other words, the event
   * invocation may be postponed, exact timings depend on implementation details of this write ahead log.
   *
   * @param lsn   the LSN to fire at.
   * @param event the event to fire.
   */
  void addEventAt(OLogSequenceNumber lsn, Runnable event);

}
