Names: coolreport, magicreport, conextreport
1. ts, reason_code, count
   reason_code, group_id reason_name (Cached)
   group_id, group_name (Cached)
   
   Output: 
   a) group_id, group_name, count
   b) reason_id, reason_name, count
   
2. Timestamp - Timestamp bucket - Half Hourly: t = {t} to {t+30*60*60} 
   Impressions - Impression bucket
   
   
3. Typed formula evaluations
   package net.atomex.coolreports.warehouse;

import org.contextreport.core.Context;
import org.contextreport.core.Record;
import org.contextreport.core.annotations.Formula;

public abstract class IDerived implements IServing {

	@Formula("${impressions} == 0 ? -1 : (${cost}*1000)/${impressions}")
	double getEcpm() {
		Record record = Context.getContext().getRecord();

		return getImpressions() == 0 ? -1
				: (getCost() * 1000 / getImpressions());
	}

}
   
   
4. 