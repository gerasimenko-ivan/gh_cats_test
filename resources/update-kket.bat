REM save as Cyrillic > OEM 866 encoding document
REM WARNING: move this file outside /nsi_test dir otherwise it will try to delete itself

taskkill /F /IM chromedriver.exe /T

REM FORFILES /P "C:\Users\Администратор\IdeaProjects\kkets_test" /S /M * /C "CMD /C DEL /Q /S @PATH"
REM FORFILES /P "C:\Users\Администратор\IdeaProjects\kkets_test" /S /M * /C "CMD /C IF @isdir == TRUE rd /Q /S @PATH"


cd C:\Users\Администратор\IdeaProjects\kkets_test
REM git clone https://repo.mos.ru/kkets_autotest_repo.git kkets_test

git checkout .
git pull origin master