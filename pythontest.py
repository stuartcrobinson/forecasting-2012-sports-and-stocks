import numpy as np
import random as r
import math as m

def initialize(context):
    
    set_slippage(slippage.VolumeShareSlippage())
    set_universe(universe.DollarVolumeUniverse(98.0,100.0))
    
    context.stocks=[]
    context.max_notional=1000
    context.min_notional=-1000
    
def handle_data(context, data):
    
    context.stocks=[sid for sid in data] 
    prices=get_prices(data, context.stocks)
    if prices is None: 
        return        
            
    rho=rho_matrix(prices)#correlation matrix
        
    #===========================================================================#
    #                 #--------Probability Assessment-------#                   #
    #===========================================================================#
    pr_dw_up=[0]*len(rho)
    pr_dw_up2=[0]*len(rho)
    for stock in range(len(context.stocks)):
        pr_dw_up[stock]=pr(prices,stock)
        pr_dw_up2[stock]=pr2(prices,stock)
        
    
    #===========================================================================#
    #                         #--------Exit-------#                             #
    #===========================================================================#
    for stock in range(len(context.stocks)):
        position=context.portfolio.positions[context.stocks[stock]].amount
        
        if abs(position)>10: 
            order(context.stocks[stock],-position)
        
    
    #===========================================================================#
    #                      #--------Order strategy-------#                      #
    #===========================================================================#
    for stock in range(len(context.stocks)):       
                                                                                
        price=data[context.stocks[stock]].price                                 
        position=context.portfolio.positions[context.stocks[stock]].amount      
        notional=position*price    
        
        mu=np.average(prices[:,stock])
        var=np.var(prices[:,stock])
        k=m.sqrt(1/(1-.75))
        ub=mu+k*m.sqrt(var) 
        lb=mu-k*m.sqrt(var)
        
        if (mu-np.var(prices[:,stock])/2)/mu<=.5:  
            if (mu-var/2)/mu<=-63:
                if pr_dw_up2[stock][1]>pr_dw_up2[stock][0]: 
                    order(context.stocks[stock],-10)
                           
                if pr_dw_up2[stock][0]>pr_dw_up2[stock][1]: 
                    order(context.stocks[stock],10)
            
            elif fire_miss(rho,stock,pr_dw_up,prices):
                if price<=ub and price>=lb:
                    if pr_dw_up[stock][1]>.75 and notional<context.max_notional:
                        order(context.stocks[stock],-100)
                    if pr_dw_up[stock][0]>.75 and notional>context.min_notional:
                        order(context.stocks[stock],100)
                        
        
        
        
      
@batch_transform(refresh_period=0, window_length=20)
#===========================================================================#
#                     #--------Price Retrieval-------#                      #
#===========================================================================#
def get_prices(datapanel,sids):
    prices = datapanel['price'].as_matrix(sids)
    return prices

#===========================================================================#
#                   #--------Correlation Matrix-------#                     #
#===========================================================================#
def rho_matrix(prices):
    return np.corrcoef(prices.T)   


#===========================================================================#
# ---Check to see if the moving averages agree with probability acessment---#
#===========================================================================#
def mavg_check(rho,prices,stock,check,r1,r2):
    shorter_s=np.average(prices[len(prices)-r1-1:len(prices)-1,stock])
    longer_s=np.average(prices[len(prices)-r2-1:len(prices)-1,stock])
    shorter_c=np.average(prices[len(prices)-r1-1:len(prices)-1,stock])
    longer_c=np.average(prices[len(prices)-r2-1:len(prices)-1,stock])
    
    if rho>.5:
        if shorter_s>longer_s and shorter_c>longer_c:
            return True
        elif shorter_s<longer_s and shorter_c<longer_c:
            return True
        else:
            return False
    if rho<-.5:
        if shorter_s<longer_s and shorter_c>longer_c:
            return True
        if shorter_s>longer_s and shorter_c<longer_c:
            return True
        else:
            return False
        

#===========================================================================#
#                 #--------Exponential Distribution-------#                 #
#===========================================================================#
def pr(prices,stock):
    mu=np.average(prices[:,stock])
    up=0
    
    for i in range (1,len(prices)):
        if prices[0,stock]>prices[i,stock]: 
            up+=1
                
    l=up*1.0/(len(prices)-2)
    
    if (mu-np.var(prices[:,stock])/2)/mu<=.2:
        return [l*m.exp(-l), 1-l*m.exp(-l)]
    else:
        return [1-l*m.exp(-l), l*m.exp(-l)]
    
    
#===========================================================================#
#                  #--------Lognormal Distribution-------#                  #
#===========================================================================#
def pr2(prices,stock):
    last_p=prices[-1,stock]
    var=np.var(prices[:,stock])
    avg=np.average(prices[:,stock])
    sigma=m.sqrt(m.log(1+(var/avg**2)))+.00000001
        
    mu=m.log((avg**2)/m.sqrt(var+avg**2))
    
    q=.5*(1+m.erf((m.log(last_p)-mu)/(m.sqrt(2)*sigma)))
    p=1-q
    
    return [p,q]
    

#===========================================================================#
#                     #--------Signal Processing-------#                    #
#===========================================================================#
def fire_miss(rho,stock,pr_dw_up,prices):
    fire=miss=0
    
    for check in range(len(rho)):
        cor=rho[stock,check]
        p=pr_dw_up[check][0]
        q=pr_dw_up[check][1]
        
        if cor>=.5 and q>p: 
            fire+=1
            if mavg_check(cor,prices,stock,check,10,20):
                fire+=1
        elif cor<=-.5 and q<p:
            fire+=1
            if mavg_check(cor,prices,stock,check,10,20):
                fire+=1
        elif cor>.5 and q<p: 
            miss+=1
            if not mavg_check(cor,prices,stock,check,10,20):
                miss+=1
        elif cor<-.5 and q>p: 
            miss+=1
            if not mavg_check(cor,prices,stock,check,10,20):
                miss+=1
                
    if fire>miss:
        return True
    if fire<miss:
        return False
