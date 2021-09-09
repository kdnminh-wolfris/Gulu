# Gulu
This is an app for our mid-term project of the subject Mobile Device Application Development (CS426) at University of Science – Vietnam National University in Ho Chi Minh City.

Gulu is an Android app scanning and recognising text from an image for translating. The reasoning behind the name "Gulu" is that it is actually a mispronounced word from "guru", which indicates our initial intention is to help students in studying, like a friendly "guru", especially in reading documents in foreign languages. Although the mechanism of this app is nothing special or new, it was a great opportunity for us to try using ML Kit.

Demo video: https://youtu.be/xdMga2Z7mIU

## Key Technologies
Java, SQL, Android Studio, ML Kit

## App Structure and Implementation
In the main screen, there is a menu of three functions: Camera, Gallery and Library.

<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132618818-b6d162e1-7f71-416c-a4c9-01953feeee5a.png" alt="Portrait Main Screen" style="width:300px"/>
</p>
<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132619081-bf13f7cc-dadf-443b-bf62-a816a49eb576.png" alt="Landscape Main Screen" style="height:300px"/>
</p>

The Camera and Gallery will send a standard intent action to have the camera application capture a picture or to pick an available image from device. When an image is returned, the image data will be sent to an image cropper using [Android Image Cropper](https://github.com/ArthurHub/Android-Image-Cropper) by ArthurHub. The cropped image will then be passed through [TextRecognizer](https://developers.google.com/android/reference/com/google/mlkit/vision/text/TextRecognizer) from ML Kit to get text from it and transfer to the [Translator](https://developers.google.com/android/reference/com/google/mlkit/nl/translate/Translator) also from ML Kit to translate gotten text.

<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132619052-3d650cb7-9911-4c5f-bad5-1dc58afd59d3.png" alt="Portrait Translation Screen" style="width:300px"/>
</p>
<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132619058-34f7accc-79f3-4c87-bfba-3a179b88da0e.png" alt="Landscape Translation Screen" style="height:300px"/>
</p>

The image and scanned text after the translation will be stored in device. When users click on Library, a list of past translations will be displayed. All database handling processes were done by using [android.database.sqlite](https://developer.android.com/reference/android/database/sqlite/package-summary) package.

<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132619117-86c49659-def5-4952-89e5-65c538072af0.png" alt="Portrait History Screen" style="width:300px"/>
</p>
<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132619125-d70a84b3-171a-4fff-8bb0-2fed15c6e61f.png" alt="Landscape History Screen" style="height:300px"/>
</p>

## App Flow
<p align="center">
  <img src="https://user-images.githubusercontent.com/29631037/132617817-c686e51f-f771-4dce-8b14-ed3a1fe5a3c7.png" alt="Gulu's App Flow"/>
</p>

## Team Members
- [Khấu Đặng Nhật Minh](https://www.linkedin.com/in/wolfris/) – (student ID: 19125011)
- Nguyễn Khánh Nguyên – (student ID: 19125109)
- Nguyễn Duy Anh Quốc – (student ID: 19125117)
