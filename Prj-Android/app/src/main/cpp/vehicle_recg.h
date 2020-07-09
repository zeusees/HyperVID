#pragma once

#ifndef __MOBILENET_DNN_H__
#define __MOBILENET_DNN_H__

#include <opencv2/opencv.hpp>
#include <string>
#include <vector>
#include <time.h>
#include <algorithm>
#include <map>
#include <iostream>


#include <opencv2/dnn.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>

using namespace cv;
using namespace dnn;

using namespace std;



class VEHICLE_RECG {

public:
	VEHICLE_RECG(const string &model_path);
	~VEHICLE_RECG();

	std::pair<std::string,float> detect(cv::Mat& img_);
	void recognize(cv::Mat& img_, cv::Rect& rect);
private:


	Net reg_net,det_net;
	
	std::vector<std::string> classes;
	const float detect_thresh = 0.6;
	const Scalar mean{ 123.68, 116.779, 103.939 };

};

#endif //__MOBILENET_DNN_H__
