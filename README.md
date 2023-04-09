# SafetyHelmetDetection

![demo](./images/demo.gif)

## 1. Platform

Android Platform 을 사용했다. 원래는 CCTV 에 적용할 생각으로 Desktop OS 를 생각하고 있었지만 접근성 때문에 Android 를 사용했다. 스마트폰은 Desktop 과 달리 Camera 를 가지고있다. 그럼으로 추가적으로 Camera 를 준비해야할 필요가 없다. 그리고 Android 스마트폰은 대한민국에서 가장 높은 점유율을 보이는 스마트폰 OS 이다. 이 시스템에 대한 접근성을 높일 수 있다. 또한 보급형 스마트폰은 IOS 를 사용하는 아이폰에 비해 가격이 저렴하다. 산업 현장은 많은 위험 요소가 존재하기에 비싼 스마트폰을 이 시스템 용도로 구매 하는 것은 비효율 적이다.

## 2. 프로젝트 구조

activity
  - Android Activity 들을 모아 놓았다.

analzer
  - PyTorch in Android 로 미리 저장된 모델을 이용해 추론하는 분석기이다.

model
  - Analzer 와 Activiy 또는 View 사이에 데이터를 전달하기 위한 데이터 모델들        이 위치해있다.

processor
  - AI Model 을 통해 얻은 array output 을 사용하기 편리한 모델로 매핑해준         다.

view
  - Activity 를 제외한 view 클래스들을 모아놓았다.

assets
  - Training 시킨 Object Detection 모델을 위치시킨다.


## 3. Class

SafetyHelmetDetectionApplication
- Application 이 구동되면 미리 모델을 메모리에 받아놓기 위한 코드가 들어있다.

CameraXActivity
- Android 의 CameraX 를 이용하기 위해 CameraX 세팅을 해놓은 추상 클래스이다.

DetectionActivity
- CameraXActivity 를 구현한 Activity Class 이다.
- 미리보기를 위한 View 와 Bounding Box 를 위한 View 를 세팅한다.
- 경고음을 선언하고 안전모 미착용한 사람이 발견되면 경고음이 울리게 구현했다.
- 이미지 Analye 를 위한 구현이 들어있다.

StartActivity
- 첫 화면을 위한 Activity

ObjectImageAnalyzer
- image 를 bitmap 으로 변환하고 PyTorch in Android 의 패키지를 이용해 Tensor 로 변환한다.
- 그리고 추론한 output 을 Processor 를 이용해 Object 모델로 변환한다.

BoundingBox
- View 에 bounding box 를 그리기 위한 데이터를 저장한다.

DetectionObject
- 추론한 결과를 담을 데이터 모델이다.

PrePostProcessor
- 추론 모델이 만들 결과 Tensor 을 DetectionObject 로 변환하는 클래스이다.

BoundingBoxDisplayVIew
- BoundingBox 모델을 받아 graphics 를 이용해 PaintRectangle 에 Bounding Box 를 그린다.

## 4. Object Detection 모델과 데이터

* Object Detection 모델: YoLOv5 (https://github.com/ultralytics/yolov5)
* Dataset: Kaggle Dataset 5000개 (https://www.kaggle.com/datasets/andrewmvd/hard-hat-detection)

## 5. 한계

스크린 녹화를 진행하며 안전 장비 미착용을 탐지하는 경우 연산 처리 속도가 느려지는 문제가 발생했다. 확실히 스마트폰의 추론 속도가 매우 느린 것을 확인할 수 있었다. 추론 속도가 느려지게되면 안전 장비 미착용한 사람이 발견돼도 한참 뒤에 경고음성이 나오는 현상이 발생했다. 그래서 스마트폰의 성능에 매우 의존적인 시스템이라는 한계를 가지고 있다. 그리고 산업 현장에서 좋은 성능의 스마트폰을 안전 장비 미착용 탐지에 쓸 가치가 있는지 모르겠다. Anroid Application 이라는 플랫폼은 접근성이 좋다는 장점이 있지만 추후에 CCTV 와 PC 를 활용하는 시스템을 개발하는 것이 더 좋은 선택지가 될 것같다.

또한 이 시스템을 활용해서 얼마나 안전 장비 착용률에 도움이 될지 테스트 해볼 방법을 찾지 못해서 실제 시스템 검증을 해보지는 않았다.
