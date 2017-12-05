*(Updated November 16, 2017)*

# open311-android-library

[![Build Status](https://travis-ci.org/CodeTanzania/open311-android-library.svg?branch=develop)](https://travis-ci.org/CodeTanzania/open311-android-library)

This is a library that can be easily ported into any android project that wishes to use the CodeTanzania/open311-api

**_At present, this library is in development._**

Currently there are two modules:
- The `app` module is a sample app that uses the library code. It consists of a simple home screen with two buttons.
- The `majiix311` module contains the library, which will eventually be made into an .aar that can be hosted on maven and incorporated into any project that wishes to access an open311-api fork.

Network calls are made using JavaRX and Retrofit. Testing is via Roboelectric. Eventually the library will contain all of the logic and activities needed to submit an issue, and view submitted issues.

## Great! How do I use it?

To use this library in your project:

### Setup

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
Currently, `setup` makes an initial call to get issue categories ("services" according to the MajiFix API) which are then cached for later use. In the future, this setup call will be used for a variety of configuration tasks. For example, custom styling for the submit issue form.

### Submitting issues

To add the ability to submit issues to your project:

Just start a `ReportProblemActivity`:
```
Intent startReportIntent = new Intent(this, ReportProblemActivity.class);
startActivity(startReportIntent);
```

### Seeing submitted issues

**_Not yet implemented_**

A `ReportProblemActivity` will be used to see submitted issues, but don't expect it to work quite yet...
```
Intent startReportIntent = new Intent(this, ProblemListActivity.class);
startActivity(startReportIntent);
```

## In Conclusion:

I've only put a few days into development so far. It is still the early days... I appreciate feedback and contributions. I will try to keep this README updated on a regular basis, but I'll admit, like many developers, sometimes I get behind on my documentation. 

## Want to contribute?

We are using gitflow to manage this project. Please see https://github.com/nvie/gitflow for more details.
There are tasks in development by the Tanzanian team. For more information take a look at our Taiga board: https://tree.taiga.io/project/krtonga-majifix/us/64?kanban-status=1396627

## License

The MIT License (MIT)

Copyright (c) 2017 CodeTanzania & Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
