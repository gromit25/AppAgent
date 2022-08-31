package com.babe.collectionwatcher;

import java.lang.ref.WeakReference;
import java.util.Collection;

class CollectionTypeInfo extends CollectionInfo<Collection<?>> {
	
	private WeakReference<Collection<?>> collectionObj;
	
	@Override
	void setCollectionObj(final Collection<?> collectionObj) {
		this.collectionObj = new WeakReference<Collection<?>>(collectionObj);
	}
	
	@Override
	Collection<?> getCollectionObj() {
		
		if(this.collectionObj != null) {
			return this.collectionObj.get();
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
