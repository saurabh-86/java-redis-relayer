#!/bin/sh
PACKAGE=fk-redis-relayer
PACKAGE_ROOT="./fk-redis-relayer-pkg"
VERSION=$GO_PIPELINE_LABEL
ARCH=all

echo "Creating temp packaging directory ${PACKAGE_ROOT} ..."
mkdir -p $PACKAGE_ROOT
mkdir -p $PACKAGE_ROOT/DEBIAN
mkdir -p $PACKAGE_ROOT/var/lib/$PACKAGE
mkdir -p $PACKAGE_ROOT/etc/$PACKAGE
mkdir -p $PACKAGE_ROOT/etc/cosmos-jmx
mkdir -p $PACKAGE_ROOT/etc/init.d
mkdir -p $PACKAGE_ROOT/var/log/flipkart/$PACKAGE

echo "Copying debian files to ${PACKAGE_ROOT} ..."
cp pkg/deb/control $PACKAGE_ROOT/DEBIAN/control
cp pkg/deb/postinst $PACKAGE_ROOT/DEBIAN/postinst
cp pkg/deb/postrm $PACKAGE_ROOT/DEBIAN/postrm
cp pkg/deb/preinst $PACKAGE_ROOT/DEBIAN/preinst
cp pkg/deb/prerm $PACKAGE_ROOT/DEBIAN/prerm
cp pkg/deb/init $PACKAGE_ROOT/etc/init.d/$PACKAGE
cp pkg/conf/covenant-relayer.json $PACKAGE_ROOT/etc/cosmos-jmx/covenant-relayer.json
cp pkg/conf/relayer.yml $PACKAGE_ROOT/etc/fk-redis-relayer/relayer.yml
cp pkg/conf/logback.xml $PACKAGE_ROOT/etc/fk-redis-relayer/logback.xml

echo "Updating version in control file ..."
sed -e "s/VERSION/${VERSION}/" -i $PACKAGE_ROOT/DEBIAN/control

echo "Building fat jar ..."
./gradlew shadow

echo "Copying jar and yml to ${PACKAGE_ROOT} ..."
cp build/libs/java-redis-relayer-1.0-all.jar $PACKAGE_ROOT/var/lib/$PACKAGE/redis-relayer-shaded.jar

echo "Building debian ..."
dpkg-deb -b $PACKAGE_ROOT

echo "Removing older debians ..."
rm -f pkg/*.deb

echo "Renaming debian ..."
mv $PACKAGE_ROOT.deb pkg/${PACKAGE}_${VERSION}_${ARCH}.deb

echo "Removing temp directory ${PACKAGE_ROOT} ..."
rm -r $PACKAGE_ROOT

echo "Done."
