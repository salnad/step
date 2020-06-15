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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {

  // Takes in a collection of all events (each with seperate attendees and timeranges) and
  // 'MeetingRequest' (with required attendees and a duration requirement) and returns a Collection
  // of TimeRanges that work with schedules of required attendees and meets the duration requirement
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result;

    ArrayList<TimeRange> conflicts = generateConflicts(events, request.getAttendees());

    if (conflicts.isEmpty()) {
      // If no conflicts, create an time range for entire day and add to result if possible (meets
      // duration requirement)
      result = new ArrayList<TimeRange>();
      TimeRange fullDay =
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
      addIfPossible(result, fullDay, request.getDuration());
    } else {
      // if conflicts exist, generate free time ranges taking into account conflicts / minimum
      // duration
      result = generateFreeTimeRanges(conflicts, request.getDuration());
    }
    return result;
  }

  // Generates arraylist of conflicting events (events attendees of requested meeting are also
  // attending)
  private ArrayList<TimeRange> generateConflicts(
      Collection<Event> events, Collection<String> attendees) {
    ArrayList<TimeRange> conflicts = new ArrayList<TimeRange>();

    for (Event possibleConflict : events) {
      // checks if set of attendees of the current 'possible conflicting event' and attendees to
      // requested event are disjoint (no shared members)
      if (!Collections.disjoint(possibleConflict.getAttendees(), attendees)) {
        // adds to conflicts if not disjoint (member in possible conflict is attending requested
        // meeting)
        conflicts.add(possibleConflict.getWhen());
      }
    }

    return conflicts;
  }

  private ArrayList<TimeRange> generateFreeTimeRanges(
      ArrayList<TimeRange> conflicts, long duration) {
    ArrayList<TimeRange> freeTimes = new ArrayList<TimeRange>();

    // merge overlapping / contained conflicts into each other, and order them by starting time
    mergeConflicts(conflicts);

    // generate TimeRange from start of day to start of first event, and add if possible
    TimeRange startToFirst =
        TimeRange.fromStartEnd(TimeRange.START_OF_DAY, conflicts.get(0).start(), false);
    addIfPossible(freeTimes, startToFirst, duration);

    // loop through pairs of adjacent conflicts, create time ranges between end of first and
    // beginning of second, and add to free times if possible
    int idx = 0;
    while (idx < conflicts.size() - 1) {
      TimeRange current = conflicts.get(idx);
      TimeRange next = conflicts.get(idx + 1);
      TimeRange possibleRange = TimeRange.fromStartEnd(current.end(), next.start(), false);
      addIfPossible(freeTimes, possibleRange, duration);
      idx++;
    }

    // generate TimeRange from end of last event to end of day, and add if possible
    TimeRange lastToEnd =
        TimeRange.fromStartEnd(
            conflicts.get(conflicts.size() - 1).end(), TimeRange.END_OF_DAY, true);
    addIfPossible(freeTimes, lastToEnd, duration);

    return freeTimes;
  }

  // Merge conflicts (if there's overlap / one conflict contains another, merges into singular
  // conflict)
  private void mergeConflicts(ArrayList<TimeRange> conflicts) {
    // Sort by starting time
    Collections.sort(conflicts, TimeRange.ORDER_BY_START);

    int idx = 0;
    while (idx < conflicts.size() - 1) {
      TimeRange current = conflicts.get(idx);
      TimeRange next = conflicts.get(idx + 1);
      if (current.overlaps(next)) {
        // if overlap between current and next event, 'merge' them (remove prev two events and
        // create new event starting at first events start time, and the maximum of the two events
        // ending time)
        TimeRange merged =
            TimeRange.fromStartEnd(current.start(), Math.max(current.end(), next.end()), false);
        conflicts.remove(idx + 1);
        conflicts.set(idx, merged);
      } else {
        // otherwise, move onto next event
        idx++;
      }
    }
  }

  // Checks if time range is valid (satisfies minimum duration requirement), and adds to collection
  // of TimeRanges if possible
  private void addIfPossible(Collection<TimeRange> collection, TimeRange range, long duration) {
    if (range.duration() >= duration) {
      collection.add(range);
    }
  }
}
