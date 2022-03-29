package toit.du.tee.grsc.rest;

import java.time.LocalDateTime;

public final class ContentBuilder {

	private LocalDateTime now = LocalDateTime.now();

	private Long id;
	private String title;
	private String content;
	private LocalDateTime created = now;
	private LocalDateTime modified = now;
	private LocalDateTime accessed = now;
	
	public ContentBuilder(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public ContentBuilder(Content content) {
		this.id = content.id();
		this.title = content.title();
		this.content = content.content();
		this.created = content.created();
		this.modified = content.modified();
		this.accessed = content.accessed();
	}
	
	public ContentBuilder id(Long id) {
		this.id = id;
		
		return this;
	}
	
	public ContentBuilder content(String content) {
		this.content = content;
		
		return this;
	}
	
	public ContentBuilder created(LocalDateTime created) {
		this.created = created;
		
		return this;
	}
	
	public ContentBuilder modified(LocalDateTime modified) {
		this.modified = modified;
		
		return this;
	}
	
	public ContentBuilder accessed(LocalDateTime accessed) {
		this.accessed = accessed;
		
		return this;
	}
	
	public Content build() {
		return new Content(id, title, content, created, modified, accessed);
	}
}
