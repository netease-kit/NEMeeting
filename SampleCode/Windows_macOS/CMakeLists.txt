cmake_minimum_required(VERSION 3.10)

project(nemeeting-sample)

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 COMPONENTS Core Quick QuickControls2 Gui Core5Compat REQUIRED)

include_directories(${CMAKE_CURRENT_LIST_DIR}/include)

file(GLOB_RECURSE NEMEETING_SAMPLE_SOURCES
    main.cpp
    nemeeting_manager.h
    nemeeting_manager.cpp
)

if (APPLE)
    add_executable(${PROJECT_NAME} MACOSX_BUNDLE ${NEMEETING_SAMPLE_SOURCES} qml.qrc)
else ()
    add_executable(${PROJECT_NAME} WIN32 ${NEMEETING_SAMPLE_SOURCES} qml.qrc)
endif ()

target_link_directories(${PROJECT_NAME} PRIVATE
    ${CMAKE_CURRENT_LIST_DIR}/lib
)

target_link_libraries(${PROJECT_NAME} PRIVATE
    Qt6::Gui
    Qt6::Quick
    Qt6::QuickControls2
    Qt6::Core5Compat
)

if (CMAKE_BUILD_TYPE MATCHES "Debug")
    target_link_libraries(${PROJECT_NAME} PRIVATE nem_hosting_moduled)
else ()
    target_link_libraries(${PROJECT_NAME} PRIVATE nem_hosting_module)
endif ()
