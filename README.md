*(Updated December 8, 2017)*

# open311-android-library **_(WIP)_**

[![Build Status](https://travis-ci.org/CodeTanzania/open311-android-library.svg?branch=develop)](https://travis-ci.org/CodeTanzania/open311-android-library)

This is a library that can be easily ported into any android project that wishes to use the CodeTanzania/open311-api

**_At present, this library is in development._**

Currently there are three modules:
- The `library-core` module contains the data models, network calls, and local db logic that is used to fetch and cache data for offline use from the majifix api. 
- The `library-ui` module depends on the `library-core` module, and contains useful ui components, that can be used out of the box. These ui elements will be able to be customized to match the app theme (still in development).
- The `sample-app` module is a sample app that shows the `library-ui` functionality. It consists of a simple home screen with buttons that will take you to different `libarary-ui` activities.

We are using JavaRX and Retrofit to make network calls, and storing data locally using MYSQL (soon to switch to Room). Unit testing is done via Roboelectric. We hope that eventually the library will contain all of the logic and activities needed to submit an issue, update an issue, and view submitted issues, online and off.

## Great! How do I use it?

Two .aar's are hosted on jitpack and can be incorporated into any project that wishes to access an open311-api fork.

To use this library in your project:

### A) Add dependancy

**1. Add jitpack to your highest level `build.gradle`.**

    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
    }

**2. Add the correct library to your app module `build.gradle`.** 

For core functionality (Includes only `library-core` module):

    dependencies {
        compile 'com.github.CodeTanzania.open311-android-library:library-core:VERSION'
    }
    
For ui elements (Includes both `library-core` and `library-ui` modules):

    dependencies {
        compile 'com.github.CodeTanzania.open311-android-library:library-ui:VERSION'
    }


To see all available versions: https://jitpack.io/#CodeTanzania/open311-android-library*

**NOTE:** *Development is still in progress. To see the latest version of the library replace `VERSION` with `develop-SNAPSHOT`, and add the following to your `build.gradle` to ensure you always have the latest:*

    configurations.all {
       resolutionStrategy.cacheChangingMOdulesFor 0, 'seconds'
    }




### B) Configure Library

Add the base endpoint of the MajiFix API endpoint to `build.gradle`:

```
android {
    defaultConfig {
        buildConfigField("String", "END_POINT", "{YOUR_ENDPOINT_HERE}")
    }
}
```

Configure and Initialize the library in your application class, or sometime prior to use:
```
MajiFix.setup(getApplicationContext());
```       
Currently, `setup` initializes the `Auth` module which ensures proper login and api token management, and makes an initial call to get issue `Categories` ("services" according to the MajiFix API and the 311 standard), which are  cached for later use. As development continues, this setup call will be used for a variety of other configuration tasks. 

### C) Make Magic! 

What sort of magic might you ask? Hopefully the sort that increases citizen feedback, institutional capacity, transparancy, blah, de blah, de blah... For example:

# Submitting issues

#####Library-Core

The `ReportService` can be used to post a new `Problem` to the api. 

To use it directly, create a `Problem` using the `Problem.Builder`. The builder will ensure that all fields required by the api are set. When the Problem is created, call: 
```
ReportService.postNewProblem(activity, problem);
``` 
When the network call has completed, result will be broadcast by the `EventHandler`. Listen for the network response like so:

```
LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                    Problem posted = intent.getParcelableExtra(EventHandler.PROBLEM_INTENT);
                    // handle success logic here
                } else {
                    // handle failure logic here
                }
            }
        },
        new IntentFilter(EventHandler.BROADCAST_REPORT_RECIEVED));
```

To avoid memory leaks, do not forget to stop listening for Broadcasts in `onPause` or `onDestroy`.

```
LocalBroadcastManager.getInstance(activity).unregisterReceiver(myBroadcastReciever);
```

#####Library-Ui

To add a report form to your project, just start a `ReportProblemActivity`.  
```
Intent startReportIntent = new Intent(this, ReportProblemActivity.class);
startActivity(startReportIntent);
```
If user is not logged in, this activity will show input fields for name and phone number, otherwise these fields will be automatically set and hidden.

# Seeing Submitted Issues
#####Library-Core
The `ReportService` can be used to fetch problems that were reported by a given phone number. To see it work, just call: 
```
ReportService.fetchProblems(context, phoneNumber);
``` 
The service will, in parallel, attempt to retrieve reported problems from the local MYSQL database, and make a api call. If problems are returned from the db, `isPreliminary` will be true. If problems are returned from the server, `isPreliminary` will be false.

If `Auth.getIstance().isLogin()`, the ApiToken of the logged in party will be used, otherwise, a default app token will be used to retrieve list.

When the network call has completed, result will be broadcast by the `EventHandler`, similar to when posting. Listen for the network response like so:

```
LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // preliminary flag is true if this data is coming from the local db and false if this data is coming from the server
                boolean isPreliminary = intent.getBooleanExtra(EventHandler.IS_PRELIMINARY_DATA,false);
                if (intent.getBooleanExtra(EventHandler.IS_SUCCESS, false)) {
                    ArrayList<Problem> problems = intent.getParcelableArrayListExtra(EventHandler.REQUEST_LIST);
                    // show problem list
                } else {
                    // show error
                }
            }
        }, new IntentFilter(EventHandler.BROADCAST_MY_PROBLEMS_FETCHED));
```

To avoid memory leaks, do not forget to stop listening for Broadcasts in `onPause` or `onDestroy`.

```
LocalBroadcastManager.getInstance(activity).unregisterReceiver(myBroadcastReciever);
```


#####Library-Core


A `ProblemListActivity` can be used to see a list of Problems. Problems are displayed in a `ViewPager` with tabs to distinguish between `Open` and `Closed` issues. If no issues are found, an `EmptyFragment` is displayed, encouraging the user to report their first issue.

Right now, `ProblemListActivity` is a child of `SecureCompatActivity` and is hardcoded to fetch the issues of a logged in user. The `SecureCompatActivity` will automatically direct users to the `SignInActivity` if not logged in or if API token has expired.

Clicking the `Fab` will redirect the user to the `ReportIssueActivity`. A a click on a problem list item will open a simple `ProblemDetailActivity`. In the future, this class is likely to be abstract, and the client app will be asked to provide a fetch data method and the relevant click listeners.

Want to try it out? Just call:
```
Intent startReportIntent = new Intent(this, ProblemListActivity.class);
startActivity(startReportIntent);
```


# Sign In
#####Library-Core
Documentation coming soon...
#####Library-Ui
Documentation coming soon...

# In Conclusion:

It is still the early days... I appreciate feedback and contributions. I will try to keep this README updated on a regular basis, but I'll admit, like many developers, sometimes I get behind on my documentation. 

## Want to contribute?

We are using gitflow to manage this project. Please see https://github.com/nvie/gitflow for more details.
There are tasks in development by the Tanzanian team. 

For more information take a look at our Taiga board: https://tree.taiga.io/project/krtonga-majifix/us/64?kanban-status=1396627

## License

The MIT License (MIT)

Copyright (c) 2017 CodeTanzania & Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
