
#TODO can this be any shorter?  need to speed up processing!
makePrettyPvalsTable <- function(){			
	namesAndPvals = as.matrix(summary(model)$coefficients[ 2:nrow(summary(model)$coefficients), "Pr(>|t|)" ])																			#do all this crap to format the team names and pvalues into a single dataframe:		
	for (i in seq_along(row.names(namesAndPvals)))	
		row.names(namesAndPvals)[i] = substr(row.names(namesAndPvals)[i], 7, nchar(row.names(namesAndPvals)[i]))		
	namesAndPvals <- data.frame( "team" = row.names(namesAndPvals), "pVal" = namesAndPvals[,1])																#namesAndPvals = namesAndPvals[!duplicated(namesAndPvals[,1]),]														# commented cuz i want both sets of pvalues.  for x vs y teams.  for model w/ no intercept, i should use the 2nd p's i thikn
	return(namesAndPvals)
}

recordAveragePValues <- function(p_x_i, p_y_i){		
	for (r in indicesOfPreviousDate) {																										# now lets go through the rows of the current day and fill in their pvals one by one		
		xTeamName = format(dCharM[r, "x_name"])																										#get x and y team names for row r, to match the pvals in namesAndPvals
		yTeamName = format(dCharM[r, "y_name"])			
		pValsRowsWithTeamX = which(namesAndPvals$team == xTeamName)
		pValsRowsWithTeamY = which(namesAndPvals$team == yTeamName)
		
		if (length(pValsRowsWithTeamX) > 1) { 	teamXpVal = (namesAndPvals$pVal[pValsRowsWithTeamX[1]] + namesAndPvals$pVal[pValsRowsWithTeamX[2]])	/2						#if a team isn't listed in the glm model summary, it might be because it's the first team.  so it gets left off the coefficients/p-values table.  but we still want to record the team that's there!  don't blow the whole analysis just cuz ATL isn't there.   if no pval found, it might be because that team doens't have any games this season.  or hasn't played in the recent past.
		} else {								teamXpVal = NA }
		
		if (length(pValsRowsWithTeamY) > 1) {	teamYpVal = (namesAndPvals$pVal[pValsRowsWithTeamY[1]] + namesAndPvals$pVal[pValsRowsWithTeamY[2]]) /2								#get the second p-value for the team. when running glm w/ no intercept.  cuz then the x p's are all way too small
		} else {								teamYpVal = NA	}
		
		dNumM[r, p_x_i] <- as.numeric(format(    as.numeric(round(teamYpVal, 9)),   digits=3    ))
		dNumM[r, p_y_i] <- as.numeric(format(    as.numeric(round(teamXpVal, 9)),   digits=3    ))
	}	
	return(dNumM)
}
recordPValues <- function(p_x_i, p_y_i){		
	for (r in indicesOfPreviousDate) {																										# now lets go through the rows of the current day and fill in their pvals one by one		
		xTeamName = format(dCharM[r, "x_name"])																										#get x and y team names for row r, to match the pvals in namesAndPvals
		yTeamName = format(dCharM[r, "y_name"])			
		pValsRowsWithTeamX = which(namesAndPvals$team == xTeamName)
		pValsRowsWithTeamY = which(namesAndPvals$team == yTeamName)
		
		if (length(pValsRowsWithTeamX) > 0) { 	teamXpVal = namesAndPvals$pVal[pValsRowsWithTeamX[1]]								#if a team isn't listed in the glm model summary, it might be because it's the first team.  so it gets left off the coefficients/p-values table.  but we still want to record the team that's there!  don't blow the whole analysis just cuz ATL isn't there.   if no pval found, it might be because that team doens't have any games this season.  or hasn't played in the recent past.
		} else {								teamXpVal = NA }
		
		if (length(pValsRowsWithTeamY) > 0) {	teamYpVal = namesAndPvals$pVal[pValsRowsWithTeamY[1]]								#get the second p-value for the team. when running glm w/ no intercept.  cuz then the x p's are all way too small
		} else {								teamYpVal = NA	}
		
		dNumM[r, p_x_i] <- as.numeric(format(    as.numeric(round(teamYpVal, 9)),   digits=3    ))
		dNumM[r, p_y_i] <- as.numeric(format(    as.numeric(round(teamXpVal, 9)),   digits=3    ))
	}	
	return(dNumM)
}
printStuff <- function(firstLine){
	print(firstLine);print(paste("length(which(lmwt > 0)): ", length(which(lmwt > 0))));
	print("lmwt[indicesOfCurrentDate]: "); print(lmwt[indicesOfCurrentDate])
	print(paste("nobs(model): ", nobs(model)));print("model: ");print(model$call);print("");print(summary(model)$coefficients);
	print("row.names(namesAndPvals) : "); print(row.names(namesAndPvals))	
	print("namesAndPvals[,1]"); 	print(namesAndPvals[,1]); 
	
}


options(digits = 3)	#this isn't doing anything in the written file
options(scipen = 6)		#bias against sci notation

ignoreOtherSeasons = TRUE											#default values
teamPriorGames = 10
decayType = "none"

timestamp = format(Sys.time(), "%H%M%S")
inputFileName = "C:\\Users\\User\\Documents\\forecasting\\data\\all\\modified_1349838403333.csv"
outputFileName = paste("C:\\Users\\User\\Documents\\forecasting\\data\\all\\r_out_", timestamp, ".csv", sep="")

setwd("C:\\Users\\User\\Documents\\forecasting\\data\\all\\")					#TODO this directory should be passed from java!									# for sink() -- for printing log file

if (length(commandArgs(TRUE)) == 6) {									#values from java via runtime call  #"+ ignoreOtherSeasons +" "+ numTeamPriorGames +" "+ decayType +" "+ rInputFileName +" "+ rOutputFileName;
	print("command args length is 5");
	if (commandArgs(TRUE)[1] == "true") ignoreOtherSeasons = TRUE;
	if (commandArgs(TRUE)[1] == "false") ignoreOtherSeasons = FALSE;
	teamPriorGames = as.numeric(commandArgs(TRUE)[2])
	decayType = commandArgs(TRUE)[3] 	
	inputFileName = commandArgs(TRUE)[4] 	
	outputFileName = commandArgs(TRUE)[5] 	
	timestamp = commandArgs(TRUE)[6]
}

logfileName = paste("r_log_", timestamp, ".txt", sep="")


print("parameters:")
print(ignoreOtherSeasons)
print(teamPriorGames)
print(decayType)
print(inputFileName)
print(outputFileName)

d  <- read.csv(file(inputFileName))															#d for data
d$date <- as.Date(d$date , format="%m-%d-%Y")												#convert date column from string to Date format
d <- d[order(d$date),]																		#sort by date, increasing, just to be sure (was probly sorted anyway)	#d <- d[which(d$ssn == "2011"),]#d <- d[which(d$x_name == "DEN")]																	#for testing
row.names(d) <- NULL																# to ensure sequential row names for d

dTemp <- d																			#begin building matrices of original data for faster processing			
dTemp$x_name <- NULL
dTemp$y_name <- NULL
dTemp$date	 <- NULL
dNumMheader <- names(dTemp)	
dNumM <- data.matrix(dTemp)
dTemp <- data.frame( d$x_name, d$y_name, d$dup )
names(dTemp) <- c( "x_name", "y_name", "dup" )
dCharM <- as.matrix(dTemp); remove(dTemp)

dFactM <- data.frame(x_name = as.factor(d$x_name), y_name = as.factor(d$y_name))

xfai 	= which(dNumMheader == "xf_a")
yfai 	= which(dNumMheader == "yf_a")

xfe2i 	= which(dNumMheader == "xf_e2")
xfei 	= which(dNumMheader == "xf_e")
xfelwri 	= which(dNumMheader == "xf_e_lwr")
xfeupri 	= which(dNumMheader == "xf_e_upr")


yfe2i 		= which(dNumMheader == "yf_e2")
yfei 		= which(dNumMheader == "yf_e")
yfelwri 	= which(dNumMheader == "yf_e_lwr")
yfeupri 	= which(dNumMheader == "yf_e_upr")


sfe2i 	= which(dNumMheader == "sf_e2")
sfei 	= which(dNumMheader == "sf_e")
sfelwri 	= which(dNumMheader == "sf_e_lwr")
sfeupri 	= which(dNumMheader == "sf_e_upr")

tfe2i 	= which(dNumMheader == "tf_e2")
tfei 	= which(dNumMheader == "tf_e")
tfelwri 	= which(dNumMheader == "tf_e_lwr")
tfeupri 	= which(dNumMheader == "tf_e_upr")

psfxi	= which(dNumMheader == "p_sf_x")
psfyi	= which(dNumMheader == "p_sf_y")
ptfxi	= which(dNumMheader == "p_tf_x")
ptfyi	= which(dNumMheader == "p_tf_y")
pxfi	= which(dNumMheader == "p_xf")
pyfi	= which(dNumMheader == "p_yf")



uniqueDays =  unique(d$date);
for ( indexOfUniqueDays in seq_along(uniqueDays) ) {												#iterating over the indices of the set of unique days
	if (indexOfUniqueDays == 1) next																#i think this skips the day if it's the first one, so that doenstn' fail if it tries to run and there's no data.  but if we're ignoreing previous seasons, then we should skip the first day of each season!  not just the first day overall.  cuz first day of the season will have no data.
	currentDate = uniqueDays[indexOfUniqueDays]														#don't use i again!  only relevant to the set of unique days
	
	sink();	print(currentDate); sink(logfileName, append=TRUE);	print(currentDate)					#sink() unsinks -- sends output back to console
	
	indicesOfCurrentDate = which(d$date == currentDate)
	firstIndexOfCurrentDate = min(indicesOfCurrentDate)	
	lastIndexOfPreviousDate = firstIndexOfCurrentDate - 1	
	indicesOfPreviousDate =  which(d$date == d$date[lastIndexOfPreviousDate])
	
	#sink();
	print("                                   previous date indices: "); print(indicesOfPreviousDate)
	print("                                   current date indices: "); print(indicesOfCurrentDate)
	
	currentSeason = d$ssn[firstIndexOfCurrentDate]
	print("                                   current season: "); print(currentSeason)
	
	firstTeamOfCurrentDay = dCharM[firstIndexOfCurrentDate, "x_name"]							# determine how far back to look.  	#firstTeamOfCurrentDay is used to determine how many days back to look.	take the first x team of the day, and use that one to build the day's weights.  can't just use a set number of days, because some weeks have much more frequent games than others	
	r = lastIndexOfPreviousDate
	count = 0
	
	while (r > 1  &  count < teamPriorGames){													#loop backwards, counting occurences of team	#break when we enter a different ssn
		if (ignoreOtherSeasons & (d$ssn[r] != currentSeason)) break 			
		if (dCharM[r, "x_name"] == firstTeamOfCurrentDay) count = count+1						#don't need to check y_name cuz games are doubled
		r = r-1
	}
	print("                                   teamPriorGames: "); print(teamPriorGames)
	
	print("                                   count: "); print(count)
	print("                                   current season: "); print(currentSeason)
	
	if (count < teamPriorGames) {
		print("                                   skipping this loop cuz Count is too small")
		next
	}
	
	if (ignoreOtherSeasons) {
		if (d$ssn[r] != currentSeason) next
	}
	
	dayLookFirstI = r
	dayLookLastI = lastIndexOfPreviousDate
	
	nonZeroWtIndices = c(dayLookFirstI:dayLookLastI)											#non-zero weight indices		
	wtsLength = length(nonZeroWtIndices)
	print("                                   wtsLength: "); print(wtsLength)
	#sink(logfileName, append=TRUE);
	
	lmwt <- rep(0, times=nrow(d));																	# Make Weights
	
	if (decayType == "none") {
		lmwt[nonZeroWtIndices] = 1	
	}
	if (decayType == "linear") {
		slope = 1/wtsLength
		for (i in nonZeroWtIndices)
			lmwt[i] = slope * (i-min(nonZeroWtIndices))			
	}						
	if (decayType == "exponential") {
		for (i in nonZeroWtIndices)
			lmwt[i] = exp( 0.02*(i - min(nonZeroWtIndices) - wtsLength + 1))	
	}	
	if (decayType == "logistic") {	
		for (i in nonZeroWtIndices)
			lmwt[i] = 1/(1+exp(-0.05*(i - min(nonZeroWtIndices) - wtsLength/2)))	
	}	
	
	if (ignoreOtherSeasons) {
		currentSeason = dNumM[firstIndexOfCurrentDate, "ssn"]
		indicesOfOtherSeasons = which(dNumM[,"ssn"] != currentSeason)
		lmwt[indicesOfOtherSeasons] = 0		
	}
	if (sum(lmwt) == 0) next
	
	#namesDataFrame <- data.frame(x_name=dFactM[,"x_name"], y_name=dFactM[,"y_name"])
	
	#  don't make a factor matrix, make individual factor arrays
	
print("lmwt: ")
print(lmwt)
#spread	
	model <- lm( dNumM[, xfai] - dNumM[, yfai] ~ x_name + y_name, data=dFactM, weight = lmwt) 	 
	
	print("lmwt[indicesOfCurrentDate]: "); print(lmwt[indicesOfCurrentDate])
	print(paste("nobs(model): ", nobs(model)));print("model: ");print(model$call);print("");print(summary(model)$coefficients);
	dNumM[indicesOfCurrentDate, sfei] = round(fitted.values(model)[indicesOfCurrentDate],2)	
	predictions <- predict(model, dFactM[indicesOfCurrentDate,], interval="confidence")
	dNumM[indicesOfCurrentDate, sfe2i] <- predictions[,"fit"]
	dNumM[indicesOfCurrentDate, sfelwri] <- predictions[,"lwr"]
	dNumM[indicesOfCurrentDate, sfeupri] <- predictions[,"upr"]
	namesAndPvals <- makePrettyPvalsTable()
	dNumM <- recordPValues(psfxi, psfyi)		
	printStuff("spread")
	
	#total
	model <- lm( dNumM[, xfai] + dNumM[, yfai] ~ x_name + y_name, data=dFactM, weight = lmwt)  	 
	
	print("lmwt[indicesOfCurrentDate]: "); print(lmwt[indicesOfCurrentDate])
	print(paste("nobs(model): ", nobs(model)));print("model: ");print(model$call);print("");print(summary(model)$coefficients); #TODO 
	dNumM[indicesOfCurrentDate, tfei] = round(fitted.values(model)[indicesOfCurrentDate],2)	
	predictions <- predict(model, dFactM[indicesOfCurrentDate,], interval="confidence")
	dNumM[indicesOfCurrentDate, tfe2i] <- predictions[,"fit"]
	dNumM[indicesOfCurrentDate, tfelwri] <- predictions[,"lwr"]
	dNumM[indicesOfCurrentDate, tfeupri] <- predictions[,"upr"]
	namesAndPvals <- makePrettyPvalsTable()
	dNumM <- recordPValues(ptfxi, ptfyi)		
	printStuff("totals")
	
	
	#single x score
	model <- lm( dNumM[, xfai] ~ x_name + y_name, data=dFactM, weight = lmwt)  	 
	dNumM[indicesOfCurrentDate, xfei] = round(fitted.values(model)[indicesOfCurrentDate],2)	
	predictions <- predict(model, dFactM[indicesOfCurrentDate,], interval="confidence")
	dNumM[indicesOfCurrentDate, xfe2i] <- predictions[,"fit"]
	dNumM[indicesOfCurrentDate, xfelwri] <- predictions[,"lwr"]
	dNumM[indicesOfCurrentDate, xfeupri] <- predictions[,"upr"]
	namesAndPvals <- makePrettyPvalsTable()
	dNumM <- recordAveragePValues(pxfi, pyfi)
	printStuff("x")
	
	
	#single y score
	model <- lm( dNumM[, yfai] ~ x_name + y_name, data=dFactM, weight = lmwt) 	 
	dNumM[indicesOfCurrentDate, yfei] = round(fitted.values(model)[indicesOfCurrentDate],2)	
	predictions <- predict(model, dFactM[indicesOfCurrentDate,], interval="confidence")
	dNumM[indicesOfCurrentDate, yfe2i] <- predictions[,"fit"]
	dNumM[indicesOfCurrentDate, yfelwri] <- predictions[,"lwr"]
	dNumM[indicesOfCurrentDate, yfeupri] <- predictions[,"upr"]
	namesAndPvals <- makePrettyPvalsTable()
	dNumM <- recordAveragePValues(pxfi, pyfi)	
	printStuff("y")
	
	
}

m <- data.frame(d$date, dCharM, dNumM)		#m for master
names(m)[1] <- "date"
write.csv(m, file = outputFileName)
print(""); print("here is m:")
m[,]
sink()
outputFileName
