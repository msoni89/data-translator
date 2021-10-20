# Data Translator

Data translator is java library use for tranaslatind data based on config provided.

## Rrequred
- Java 8
- Maven 3.6.3

## BUild steps
- Download git project from [GIT](https://github.com/msoni89/file-reader-multi-threading)
- Run below commands
```java
cd data-translator
mvn clean compile istall // compile
mvn package // package
mkdir new_folder // for creating new package
cp ~/target/data-translator-1.0-SNAPSHOT.jar .
mkdir data // data folder for config and output file.
```




```java
java -jar data-translator-1.0-SNAPSHOT.jar --vendor-data=data/vendor_data.txt  --column-mapping=data/columns_translate.txt --extract-row=data/extract_rows.txt  --output=data/output.txt
```

## Expected output

```java

2021-10-20 16:44:17,747 main ERROR Invalid status level specified: INFO,ERROR. Defaulting to ERROR.

WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.
2021-10-20 16:44:17.845 [Thread: main] INFO [com.alveotech.FileTranslatorApplication][doStart] - Inside File TranslatorApplication start() method, arguments are [--vendor-data=data/vendor_data.txt, --column-mapping=data/columns_translate.txt, --extract-row=data/extract_rows.txt, --output=data/output.txt]
2021-10-20 16:44:17.846 [Thread: main] INFO [com.alveotech.utils.ValidatorUtilis][parseArguments] - Inside ValidatorUtils parseArguments() method
2021-10-20 16:44:17.847 [Thread: main] INFO [com.alveotech.utils.ValidatorUtilis][validateMandatoryArguments] - Inside validateMandatoryArguments, parsed arguments
2021-10-20 16:44:17.848 [Thread: main] INFO [com.alveotech.utils.ValidatorUtilis][validatePath] - Inside validatePath, provided filePaths
2021-10-20 16:44:17.850 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][<init>] - Updating passed path and loading config files into cache
2021-10-20 16:44:17.850 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][<init>] - loading columns mapping file into cache
data/columns_translate.txt
2021-10-20 16:44:17.850 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][readFile] - Reading file columns_translate.txt
2021-10-20 16:44:17.851 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][<init>] - loading extract rows mapping file into cache
2021-10-20 16:44:17.851 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][readFile] - Reading file extract_rows.txt
2021-10-20 16:44:17.851 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][<init>] - loading config file into cache finished
2021-10-20 16:44:17.851 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Inside translate() method
2021-10-20 16:44:17.851 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Opening input/output file streams
2021-10-20 16:44:17.852 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Reading first line for columns mapping
2021-10-20 16:44:17.852 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Translating column
2021-10-20 16:44:17.852 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][getTranslatedColumn] - Translating column based on config file mapping
2021-10-20 16:44:17.853 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Creating column positioning map
2021-10-20 16:44:17.853 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][getColumnsPositionIndexMap] - Creating column index map
2021-10-20 16:44:17.854 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Submitting task to reader and writer service
2021-10-20 16:44:17.870 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Waiting for task or completion
2021-10-20 16:44:17.872 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Tasks completed without any error
2021-10-20 16:44:17.872 [Thread: main] INFO [com.alveotech.service.TranslatorServiceImpl][doTranslate] - Executor stopped
2021-10-20 16:44:17.873 [Thread: main] INFO [com.alveotech.FileTranslatorApplication][doStart] - ...Translation finished...
superfluid@superfluid-XPS-13-9310:~/Documents/workspace/testing_files$ 

```

![image](https://user-images.githubusercontent.com/65469737/138109014-e611f475-e3ea-4d19-a414-4ba462d4f50b.png)

