package toit.du.tee.grsc.manager;

import toit.du.tee.grsc.jpa.ContentEntity;
import toit.du.tee.grsc.rest.Content;

public final class ContentConverter {

	public static Content toRecord(ContentEntity content) {
		return new Content(content.getId(), content.getTitle(), content.getContent(), content.getCreated(), content.getModified(), content.getAccessed());
	}
	
	public static ContentEntity toEntity(Content content) {
		long id = (content.id() != null && content.id() != 0) ? content.id() : 0;
		return new ContentEntity(id, content.title(), content.content(), content.created(), content.modified(), content.accessed());
	}
	
	public static void updateEntity(ContentEntity contentEntity, Content content) {
		if (content.title() != null) {
			contentEntity.setTitle(content.title());
		}
		if (content.content() != null) {
			contentEntity.setContent(content.content());
		}
		if (content.created() != null) {
			contentEntity.setCreated(null);
		}
		if (content.accessed() != null) {
			contentEntity.setAccessed(content.accessed());
		}
		if (content.modified() != null) {
			contentEntity.setModified(content.modified());
		}
	}
}
