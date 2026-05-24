#!/bin/sh
# Gradle wrapper script - 完整版见官方发行版
# 此处使用简化版，但实际工作流中会使用 Actions 内置的 gradle-wrapper
# 为防万一，提供一个最小可执行脚本
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Gradle wrapper jar not found. Please run 'gradle wrapper' first."
    exit 1
fi
exec java -cp "gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
