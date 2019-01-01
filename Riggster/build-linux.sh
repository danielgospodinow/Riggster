java -jar packr.jar packr-linux-config.json
echo Fixing some folding ...
cd out-linux
mv assets/* ./
rmdir assets/
echo Done!