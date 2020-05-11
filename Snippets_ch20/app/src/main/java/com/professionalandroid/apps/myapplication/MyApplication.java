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

import android.app.Application;
import android.os.StrictMode;

/*
 * Listing 20-3: Enabling Strict Mode for an application
 */
public class MyApplication extends Application {

  public static final boolean DEVELOPER_MODE = true;

  @Override
  public final void onCreate() {
    super.onCreate();
    if (DEVELOPER_MODE) {
      StrictMode.enableDefaults();
    }
  }
}