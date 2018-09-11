REM save as Cyrillic > OEM 866 encoding document
REM WARNING: move this file outside /pir_test dir otherwise it will try to delete itself

call C:\Users\Администратор\IdeaProjects\kkets_test_bat\update-kket.bat

cd "C:\Users\Администратор\IdeaProjects\kkets_test" && mvn clean test -Dtestdatafile=test2-env.json -Dsuite=test-suite/all-tests.xml site