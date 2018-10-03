# Available Commands to Run with Selenium Automation Bundle

In this section of Selenium Automation Bundle documentation, you'll find all the currently available commands.

If you decide to create your own command, consult [Creating Commands.md].

## Automatic Generation of TestNG XML Configuration File

Selenium Automation Bundle simplifies generation of `testng.xml` file for you. You can run the command below to create
a configuration file from the command line:

The command `generateTestNG` can accept three parameters:

| Parameter    | Value        | Description                                       |
| ------------ | ------------ | ------------------------------------------------- |
| environments | List<String> | List of platforms (operating systems) and browser |
| devices      | List<String> | List of mobile devices                            |
| parallel     | List<String> | List of MongoDB connection string parameters      |
| configName   | List<String> | Name for the created TestNG configuration file    |

### Environments

You can pass the following arguments for the `environments` parameter:

* The operating system: `linux`, `mac`, or `windows`
* The browser to run your tests: `chrome`, `firefox`, `safari`, `MicrosoftEdge`

**NOTE**: All browsers but Microsoft Edge are written in lowercase letters.

The following example will generate a TestNG configuration file for running tests on MacOS in Safari and Chrome, and on
Windows in Microsoft Edge:

```bash
./gradlew run -Pcommand="generateTestNG -environments=mac:safari,mac:chrome,windows:firefox,windows:MicrosoftEdge"
```

### Devices

You can pass a list of devices as arguments to `devices`, for example:

```bash
./gradlew run -Pcommand="generateTestNG -devices=iphone5"
```

## MongoDB Commands

If you're going to use MongoDB to store your test data, Selenium Automation Bundle provides a few commands to run
MongoDB tasks.

### Dump MongoDB

You can create a dump file of your MongoDB database with the following command:

```bash
./gradlew run -Pcommand=mongoDump
```

The command `mongoDump` can accept three parameters:

| Parameter        | Value        | Description                                    |
| ---------------- | ------------ | ---------------------------------------------- |
| collections      | List<String> | List of database collections you want to dump  |
| subPath          | List<String> | List of sub-paths to the dump directory        |
| connectionString | List<String> | List of MongoDB [connection string parameters] |

You can run the `mongoDump` command with the parameters this way:

```bash
./gradlew run -Pcommand="mongoDump -collections=appMainPageScreenshots"
```

### Restore MongoDB

You can restore your MongoDB database from a given dump using the following command:

```bash
./gradlew run -Pcommand=mongoRestore
```

For the parameters, consult the [Dump MongoDB section](#dump-mongodb-section).

## UI Comparison Module

Selenium Automation Bundle has a few commands that you can use to carry out various actions with the screenshots. For
example, you may need to download baseline screenshots from Dropbox or replace the old baseline screenshots with the new
ones.

All the commands related to managing screenshots are located in the `main/.../uicomparison/commands/` directory. In the
three sections below, we'll review the commands you can use with the bundle.

### Updating Baseline Screenshots

When you need to replace the _baseline_ screenshots with the new _actual_ screenshots, use the following command:

```bash
./gradlew -Pcommand=updateScreenshots
```

### Loading Screenshots to and from Dropbox

The bundle provides two commands `downloadFromDropbox` and `uploadToDropbox` to let you move your screenshots between
the development machine and remote storage. You can also specify the category of the screenshots that you can download
or upload.

This is how you can download the screenshots from Dropbox:

```bash
./gradlew -Pcommand="downloadFromDropbox -category=baseline"
```

The command above will let you download the baseline screenshots from Dropbox. If you need to download all types of the
screenshots, pass a respective argument &ndash; `actual`, `difference`, or `baseline` &ndash; to this command one by one
separating the arguments by commas.

Uploading screenshots to Dropbox looks almost exactly the same:

```bash
./gradlew -Pcommand="uploadToDropbox -category=baseline"
```

Similarly to `downloadFromDropbox`, you can pass up to three arguments to `category`.

[creating commands.md]:
[connection string parameters]: https://docs.mongodb.com/manual/reference/connection-string/