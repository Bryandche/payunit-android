# PayUnit Android Library

The **PayUnit Android Library** provides a customizable button for processing payments in Android applications. This library simplifies the integration of payment processing features into your app.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
    - [XML Usage](#xml-usage)
    - [Java Usage](#java-usage)
    - [Kotlin Usage](#kotlin-usage)
- [License](#license)

## Installation

To use the PayUnit library in your Android project, follow these steps:

1. **Add JitPack to your project-level `build.gradle` file:**

   ```groovy
   allprojects {
       repositories {
           google()
           mavenCentral()
           maven { url 'https://jitpack.io' }
       }
   }
   ```

2. **Add the dependency to your app-level `build.gradle` file:**

   ```groovy
   dependencies {
       implementation 'com.github.yourusername:payunit_android:Tag'
   }
   ```

## Usage

### XML Usage

To use the `PayUnitButton` in your XML layout, add the following code:
