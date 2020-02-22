## SNPE

Qualcomm prohibits redestribution of their libraries, so you have to register there and download them by yourself. `¯\_(ツ)_/¯`

1. Register and download zip from [developer.qualcomm.com](https://developer.qualcomm.com/software/qualcomm-neural-processing-sdk);
2. Copy `android/snpe-release.aar` from archive into `snpe/libs`.

**OR** compile without snpe

1. Remove `, ':snpe'` from `settings.gradle`;
2. Remove `implementation project(path: ':snpe')` from `app/build.gradle`;
3. Remove amy mentions of SNPE in MainActivity.kt.