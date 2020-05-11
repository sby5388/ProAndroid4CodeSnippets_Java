/*
 * Professional Android, 4th Edition
 * Reto Meier and Ian Lake
 * Copyright 2018 John Wiley Wiley & Sons, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.professionalandroid.apps.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/*
 * Listing 15-18: Creating a Geofence Broadcast Receiver
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = "GeofenceReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

    if (geofencingEvent.hasError()) {
      int errorCode = geofencingEvent.getErrorCode();
      String errorMessage = GeofenceStatusCodes.getStatusCodeString(errorCode);
      Log.e(TAG, errorMessage);
    } else {
      // Get the transition type.
      int geofenceTransition = geofencingEvent.getGeofenceTransition();

      // A single event can trigger multiple geofences.
      // Get the geofences that were triggered.
      List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

      // TODO React to the Geofence(s) transition(s).
    }
  }
}
