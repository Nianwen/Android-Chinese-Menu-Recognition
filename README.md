Android-Chinese-Menu-Recognition
================================

A mobile application using Chinese OCR 

I. INTRODUCTION
The motivation of Android Chinese Menu Recognition application is to help tourists
navigate in a foreign language environment. The application we developed enables the
users to get text translate as ease as a button click. The camera captures the dishes name
from the Chinese menu and returns the translated English food name as well as the
corresponding picture of that dish. The system we developed includes automatic text
detection, rotation(midtern), segmentation, OCR (optical character recognition), and text
translation. Although the current version of our application is limited to translation from
Chinese to English, it can be easily extended into a much wider range of language sets.

II ENVIRONMENT SETUP
1)We work on Ubuntu 10.04 operation system
2)Install proper IDE, such as Eclipse Helios for java.
3)Download the Android SDK r16 package and Android NDK r7 package. You would also
need proper PATH variables added. The detailed information can be found at
http://developer.android.com/sdk/index.html
4)Install the ADT Plugin for Eclipse. The detailed information can also be found at
http://developer.android.com/sdk/eclipse-adt.html
5)Open Eclipse Windows-->SDK Manager, then add Android platforms and other packages
to your SDK(Our phone device is HTC desire, so we use API 10)
6)We use Tesseract Android Tools called Tess Two project as library. They are based on the
Tesseract OCR Engine and Leptonica image processing libraries. To build this project you
should using the command below after you downloaded it:
cd <project-directory>/tess-two
ndk-build
android update project --path
ant release
7)Then import the Tess Two project as a library in Eclipse. Remember to Fix project
properties when first import it.
8)Configure your own created project to use the tess-two project as a library project.
9)Download OpenCV in andoid because we will use it in our project for grayscale and
edge detection. the detailed setup information can be found at
http://opencv.itseez.com/index.html . We use the opencv-2.3.1
10) The same as step 7, we import the OpenCV project as a library of your own project in
Eclipse.
11) Download chi_sim.traineddata package of tesseract at
http://code.google.com/p/tesseract-ocr/downloads/list. It is simplfied Chinese trained data.
Create a tessdata folder under assets in your project. Unzip it and put the trained data file
named 'chi_sim.traineddata ' to tessdata folder.

III. SYSTEM FLOW
In this paper, we propose a text detection / recognition / translation algorithm that
consists of following steps:
1) Grayscale the picture
2) Binarization
3) Canny edge detection
4) Image Rotation(midtern)
5) Segmentation
6) Optical character recognition
7) Text translation
8) Display of the translation and correponding pictures
