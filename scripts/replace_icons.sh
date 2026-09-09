#!/bin/bash
IMG_PATH="/Users/vlad/IdeaProjects/hirify-analytics/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png"
OUT_DIR="/Users/vlad/IdeaProjects/hirify-analytics/app-desktop/src/jvmMain/resources"

mkdir -p "$OUT_DIR"

python3 -m venv venv_icon
source venv_icon/bin/activate
pip install Pillow

# Generate PNG and ICO from the iOS app icon
python3 -c "
import sys
from PIL import Image

img = Image.open('$IMG_PATH')
img.save('$OUT_DIR/icon.png', format='PNG')
img.save('$OUT_DIR/icon.ico', format='ICO', sizes=[(256, 256), (128, 128), (64, 64), (32, 32), (16, 16)])
"

# Generate ICNS using the new PNG
ICNS_DIR="/tmp/MyIcon.iconset"
mkdir -p "$ICNS_DIR"
sips -z 16 16   "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_16x16.png" > /dev/null
sips -z 32 32   "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_16x16@2x.png" > /dev/null
sips -z 32 32   "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_32x32.png" > /dev/null
sips -z 64 64   "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_32x32@2x.png" > /dev/null
sips -z 128 128 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_128x128.png" > /dev/null
sips -z 256 256 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_128x128@2x.png" > /dev/null
sips -z 256 256 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_256x256.png" > /dev/null
sips -z 512 512 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_256x256@2x.png" > /dev/null
sips -z 512 512 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_512x512.png" > /dev/null
sips -z 1024 1024 "$OUT_DIR/icon.png" --out "$ICNS_DIR/icon_512x512@2x.png" > /dev/null

iconutil -c icns "$ICNS_DIR" -o "$OUT_DIR/icon.icns"
rm -R "$ICNS_DIR"

deactivate
rm -rf venv_icon
echo 'Done converting iOS app icon to desktop icons!'
