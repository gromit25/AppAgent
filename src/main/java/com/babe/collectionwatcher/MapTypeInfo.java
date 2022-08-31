package com.babe.collectionwatcher;

import java.lang.ref.WeakReference;
import java.util.Map;

class MapTypeInfo extends CollectionInfo<Map<?, ?>> {
	
	private WeakReference<Map<?, ?>> mapObj;
	
	@Override
	void setCollectionObj(final Map<?, ?> mapObj) {
		this.mapObj = new WeakReference<Map<?, ?>>(mapObj);
	}

	@Override
	Map<?, ?> getCollectionObj() {
		if(this.mapObj != null) {
			return this.mapObj.get();
		} else {
			return null;
		}
	}

	@Override
	int size() {
		
		if(this.getCollectionObj() != null) {
			return this.getCollectionObj().size();
		} else {
			return 0;
		}
	}

}
