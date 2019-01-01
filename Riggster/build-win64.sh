java -jar packr.jar packr-win64-config.json
echo Fixing some folding ...
cd out-win64
mv assets/* ./
rmdir assets/
echo Done!