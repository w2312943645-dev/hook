#!/usr/bin/env bash
# scripts/inject-xposed-meta.sh — post-build: add LSPosed module metadata to an APK.
#
#   usage: inject-xposed-meta.sh input.apk [output.apk]
#
# Does:   unzip input -> copy module/META-INF/xposed/java_init.list + module.prop
#         -> re-zip -> optional zipalign (when available).
# Does NOT: sign. INJECTION BREAKS ANY EXISTING SIGNATURE (v1/v2);
#         always re-sign afterwards with your own keystore before installing:
#             zipalign -p 4 out.apk aligned.apk
#             apksigner sign --ks my.keystore --ks-key-alias myalias aligned.apk
#
# Layout notes: java_init.list = one hook class FQN per line. Some LSPosed builds read
# module.prop under META-INF/xposed, others at archive root; this project writes the
# root (classic layout) — adjust for your target host. Scope: manager-side checkbox.
set -eu
IN="${1:?usage: inject-xposed-meta.sh input.apk [output.apk]}"
OUT="${2:-$(basename "$IN" .apk).with-meta.apk}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC="$(cd "$HERE/.." && pwd)/module"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
command -v unzip >/dev/null || { echo "needs unzip" >&2; exit 1; }
command -v zip >/dev/null || { echo "needs zip" >&2; exit 1; }
[ -e "$SRC/META-INF/xposed/java_init.list" ] || { echo "missing java_init.list" >&2; exit 1; }
unzip -q -o "$IN" -d "$WORK" || { echo "unzip failed: $IN" >&2; exit 1; }
if [ -e "$WORK/META-INF/xposed" ]; then echo "refusing double-inject" >&2; exit 1; fi
mkdir -p "$WORK/META-INF/xposed"
cp "$SRC/META-INF/xposed/java_init.list" "$WORK/META-INF/xposed/"
cp "$SRC/module.prop" "$WORK/module.prop"
( cd "$WORK" && find . -mindepth 1 -print0 | sort -z | zip -q -X -r "$OUT" -@ )
chmod 644 "$OUT" 2>/dev/null || true
if command -v zipalign >/dev/null; then ALIGNED="$OUT.aligned"; zipalign -p 4 "$OUT" "$ALIGNED" && mv -f "$ALIGNED" "$OUT"; fi
echo "injected -> $OUT (NOT SIGNED — re-sign before install)" >&2
