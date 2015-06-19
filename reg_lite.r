inputFileName = "C:\\Users\\User\\Documents\\forecasting\\data\\all\\modified_1349294556321.csv"

d  <- read.csv(file(inputFileName))			

model <- glm(d$xf_a + d$yf_a	~	  d[,"x_name"] + d[,"y_name"] , family=gaussian) 
d[, "sf_e"] = round(fitted.values(model)[],2)

model1 <- glm(d[, "xf_a"] + d[, "yf_a"]	~	  d[,"x_name"] + d[,"y_name"] , family=gaussian) 
d[, "sf_e"] = round(fitted.values(model)[],2)

model2 <- glm(d[, "xf_a"]	~	  d[,"x_name"] + d[,"y_name"] , family=gaussian)
d[, "p_sf_x"] = round(fitted.values(model2)[],2)

model3 <- glm(d[, "yf_a"]	~	  d[,"x_name"] + d[,"y_name"] , family=gaussian)
d[, "p_sf_y"] = round(fitted.values(model3)[],2)


d[, "tf_e"] = d[, "p_sf_x"] + d[, "p_sf_y"] 
