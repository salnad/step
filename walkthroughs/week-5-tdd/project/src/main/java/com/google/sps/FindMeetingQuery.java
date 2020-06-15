// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> conflicts = new ArrayList<TimeRange>();
    for (Event e : events) {
      if (!Collections.disjoint(e.getAttendees(), request.getAttendees())) {
        conflicts.add(e.getWhen());
      }
    }

    // System.out.println("BEFORE");
    // for (TimeRange t : conflicts) {
    //   System.out.println(t.toString());
    // }

    if (conflicts.isEmpty()) {
      Collection<TimeRange> res = new ArrayList<TimeRange>();
      TimeRange possible = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
      if (possible.duration() >= request.getDuration()) {
        res.add(possible);
      }
      return res;
    }

    Collections.sort(conflicts, TimeRange.ORDER_BY_START);
    int curr = 0;
    while (curr < conflicts.size() - 1) {
      TimeRange current = conflicts.get(curr);
      TimeRange next = conflicts.get(curr+1);
      
      if (current.overlaps(next)) {
        TimeRange merged = TimeRange.fromStartEnd(current.start(), Math.max(current.end(), next.end()), false);
        conflicts.remove(curr+1);
        conflicts.set(curr,merged);
      } else {
        curr++;
      }
    }

    Collection<TimeRange> result = new HashSet<TimeRange>();
    if (!conflicts.isEmpty()) {
      TimeRange startToFirst = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, conflicts.get(0).start(), false);
      if (startToFirst.duration() >= request.getDuration()) {
        result.add(startToFirst);
      }
      curr = 0;
      while (curr < conflicts.size() - 1) {
        TimeRange current = conflicts.get(curr);
        TimeRange next = conflicts.get(curr + 1);
        TimeRange possibleRange = TimeRange.fromStartEnd(current.end(), next.start(), false);
        if (possibleRange.duration() >= request.getDuration()) {
          result.add(possibleRange);
        }
        curr++;
      }
      TimeRange lastToEnd = TimeRange.fromStartEnd(conflicts.get(conflicts.size() - 1).end(), TimeRange.END_OF_DAY, true);
      if (lastToEnd.duration() >= request.getDuration()) {
        result.add(lastToEnd);
      }
    }
    ArrayList<TimeRange> res = new ArrayList<TimeRange>(result);
    Collections.sort(res, TimeRange.ORDER_BY_START);
    return res;


    // System.out.println("AFTER");
    // for (TimeRange t : conflicts) {
    //   System.out.println(t.toString());
    // }
    
    // throw new UnsupportedOperationException("TODO: Implement this method.");
  }
}
