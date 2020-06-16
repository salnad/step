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

/**
* Queries calendar to return possible meeting TimeRange's for a meeting request.
* 
* <p>Contains the query method (used to query calendar with a meeting request) along with helper 
* methods to aid in querying the calendar.
*/
public final class FindMeetingQuery {

  /**
   * Returns TimeRange's which are available to schedule meetings.
   *
   * <p>Availability is detirmined by the meeting request. All 'available' TimeRange's must be at
   * least the length of the requests duration requirement, must not conflict with the required
   * attendees other events, and should conflict with as few as possible events of the optional
   * attendees.
   *
   * @param events a collection of all other events on the calendar
   * @param request a list of required attendees, optional attendees, and the duration requirement
   *     for the meeting
   * @return Collection<TimeRange> of available TimeRange's for the meeting to be scheduled
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> result;

    ArrayList<TimeRange> conflicts = generateConflicts(events, request.getAttendees());

    if (conflicts.isEmpty()) {
      result = new ArrayList<TimeRange>();
      // if no conflicts exist, attempt to book the entire day
      TimeRange fullDay =
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true);
      addIfPossible(result, fullDay, request.getDuration());
    } else {
      result = generateFreeTimeRanges(conflicts, request.getDuration());
    }
    return result;
  }

  /**
   * Generates an arraylist of conflicting events.
   *
   * <p>Conflicting events consist of events who's attendees intersect with the required attendees
   * of the meeting request.
   *
   * @param events a collection of all other events on the calendar
   * @param attendees a list of required attendees
   * @return all events TimeRange's that conflict
   */
  private ArrayList<TimeRange> generateConflicts(
      Collection<Event> events, Collection<String> attendees) {
    ArrayList<TimeRange> conflicts = new ArrayList<TimeRange>();

    for (Event possibleConflict : events) {
      // checks if current event's / required attendees intersect
      if (!Collections.disjoint(possibleConflict.getAttendees(), attendees)) {
        conflicts.add(possibleConflict.getWhen());
      }
    }

    return conflicts;
  }

  /**
   * Generates an ArrayList of times available for the meeting.
   *
   * <p>Available TimeRange's must not intersect with TimeRange's in conflicts and must be at least
   * as long as the provided duration.
   *
   * @param conflicts a collection of TimeRanges that conflict
   * @param duration the required duration of the meeting
   * @return all available TimeRange's for the meeting
   */
  private ArrayList<TimeRange> generateFreeTimeRanges(
      ArrayList<TimeRange> conflicts, long duration) {
    ArrayList<TimeRange> freeTimes = new ArrayList<TimeRange>();
    mergeConflicts(conflicts);

    // Handling TimeRange from start of the day to beggining of first event
    TimeRange startToFirst =
        TimeRange.fromStartEnd(TimeRange.START_OF_DAY, conflicts.get(0).start(), false);
    addIfPossible(freeTimes, startToFirst, duration);

    // Handling all TimeRange's between the days events
    int idx = 0;
    while (idx < conflicts.size() - 1) {
      TimeRange current = conflicts.get(idx);
      TimeRange next = conflicts.get(idx + 1);
      TimeRange possibleRange = TimeRange.fromStartEnd(current.end(), next.start(), false);
      addIfPossible(freeTimes, possibleRange, duration);
      idx++;
    }

    // Handling TimeRange from end of last event to end of the day
    TimeRange lastToEnd =
        TimeRange.fromStartEnd(
            conflicts.get(conflicts.size() - 1).end(), TimeRange.END_OF_DAY, true);
    addIfPossible(freeTimes, lastToEnd, duration);

    return freeTimes;
  }

  /**
   * Merges all overlapping and nested events.
   *
   * @param conflicts a list of potential conflicting events
   * @return none
   */
  private void mergeConflicts(ArrayList<TimeRange> conflicts) {
    Collections.sort(conflicts, TimeRange.ORDER_BY_START);
    // loop through all pairs of adjacent events
    int idx = 0;
    while (idx < conflicts.size() - 1) {
      TimeRange current = conflicts.get(idx);
      TimeRange next = conflicts.get(idx + 1);
      if (current.overlaps(next)) {
        int mergedEnd = Math.max(current.end(), next.end());
        TimeRange merged = TimeRange.fromStartEnd(current.start(), mergedEnd, false);
        conflicts.remove(idx + 1);
        conflicts.set(idx, merged);
      } else {
        idx++;
      }
    }
  }

  /**
   * Adds range to collection if duration of range is long enough.
   *
   * @param collection collection of timeranges to be potentially modified
   * @param range potential TimeRange to be added to collection
   * @param duration required duration TimeRange must be at least as big as
   * @return none
   */
  private void addIfPossible(Collection<TimeRange> collection, TimeRange range, long duration) {
    if (range.duration() >= duration) {
      collection.add(range);
    }
  }
}
