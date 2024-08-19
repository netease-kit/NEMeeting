cmake_minimum_required(VERSION 3.5)

project(meeting_sdk_example_qt VERSION 0.1 LANGUAGES CXX)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_INCLUDE_CURRENT_DIR ON)

find_package(QT NAMES Qt6 Qt5 REQUIRED COMPONENTS Widgets LinguistTools)
find_package(Qt${QT_VERSION_MAJOR} REQUIRED COMPONENTS Widgets LinguistTools)

include_directories(
    ${CMAKE_CURRENT_LIST_DIR}/sdk/include
    ${CMAKE_CURRENT_LIST_DIR}/reflection
    ${CMAKE_CURRENT_LIST_DIR}/sdk_wapper
)

link_directories(${CMAKE_CURRENT_LIST_DIR}/sdk/lib)

set(TS_FILES meeting_sdk_example_qt_zh_CN.ts)

configure_file(
    ${CMAKE_CURRENT_LIST_DIR}/version.h.in
    ${CMAKE_CURRENT_LIST_DIR}/version.h
)

file(GLOB MEETING_SRC *.h *.cpp)
file(GLOB SDK_INTERFACE ${CMAKE_CURRENT_LIST_DIR}/sdk/include/*.h)
#file(GLOB SDK_SRC
#    ${CMAKE_CURRENT_LIST_DIR}/sdk_wapper/*.h
#    ${CMAKE_CURRENT_LIST_DIR}/sdk_wapper/*.cpp
#)

file(GLOB UI_LIST *.ui)

set(PROJECT_SOURCES
        ${MEETING_SRC}
        ${SDK_INTERFACE}
        ${UI_LIST}
        ${TS_FILES}
)

if(${QT_VERSION_MAJOR} GREATER_EQUAL 6)
    qt_add_executable(meeting_sdk_example_qt
        MANUAL_FINALIZATION
        ${PROJECT_SOURCES}
    )
# Define target properties for Android with meeting_sdk_example_qt_qt 6 as:
#    set_property(TARGET meeting_sdk_example_qt_qt APPEND PROPERTY QT_ANDROID_PACKAGE_SOURCE_DIR
#                 ${CMAKE_CURRENT_SOURCE_DIR}/android)
# For more information, see https://doc.qt.io/qt-6/qt-add-executable.html#target-creation

qt_create_translation(QM_FILES ${CMAKE_SOURCE_DIR} ${TS_FILES})
else()
    if(ANDROID)
        add_library(meeting_sdk_example_qt SHARED
            ${PROJECT_SOURCES}
        )
# Define properties for Android with meeting_sdk_example_qt_qt 5 after find_package() calls as:
#    set(ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/android")
    else()
        add_executable(meeting_sdk_example_qt
            ${PROJECT_SOURCES}
        )
    endif()

    qt5_create_translation(QM_FILES ${CMAKE_SOURCE_DIR} ${TS_FILES})
endif()

# target_link_libraries(${PROJECT_NAME} PRIVATE -Wl,--start-group)
target_link_libraries(meeting_sdk_example_qt PRIVATE
    Qt${QT_VERSION_MAJOR}::Widgets
    nemeet_base
)
# target_link_libraries(${PROJECT_NAME} PRIVATE -Wl,--end-group)

# meeting_sdk_example_qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since meeting_sdk_example_qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
if(${QT_VERSION} VERSION_LESS 6.1.0)
  set(BUNDLE_ID_OPTION MACOSX_BUNDLE_GUI_IDENTIFIER com.example.meeting_sdk_example_qt)
endif()
set_target_properties(meeting_sdk_example_qt PROPERTIES
    ${BUNDLE_ID_OPTION}
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
    MACOSX_BUNDLE_INFO_PLIST ./MacOSXBundleInfo.plist.in
)

include(GNUInstallDirs)
install(TARGETS meeting_sdk_example_qt
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

if(QT_VERSION_MAJOR EQUAL 6)
    qt_finalize_executable(meeting_sdk_example_qt)
endif()
