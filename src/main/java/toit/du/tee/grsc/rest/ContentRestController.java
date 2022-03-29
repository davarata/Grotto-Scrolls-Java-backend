package toit.du.tee.grsc.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import toit.du.tee.grsc.manager.ContentManager;

@RestController
@RequestMapping("/content")
@CrossOrigin
public class ContentRestController {

	@Autowired
	private ContentManager contentManager;

	@PutMapping("/create")
	@ResponseBody
	public Content createContent(@RequestBody Content content) {
		return contentManager.createContent(content);
	}
	
	@GetMapping("/read/{id}")
	@ResponseBody
	public Content readContent(@PathVariable long id) {
		return contentManager.readContent(id);
	}
	
	@PostMapping("/update")
	public void updateContent(@RequestBody Content content) {
		contentManager.updateContent(content);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteContent(@PathVariable long id) {
		contentManager.deleteContent(id);
	}
	
	@GetMapping("/list")
	@ResponseBody
	public List<Content> getList() {
		return contentManager.getList();
	}
	
	@GetMapping("searchTitles/{searchStr}")
	@ResponseBody
	public List<Content> searchContent(@PathVariable String searchStr) {
	   return contentManager.searchByTitle(searchStr);
	}
	
   @GetMapping("/formatted/{id}")
   @ResponseBody
   public List<Node> getFormattedContent(@PathVariable long id) {
      return contentManager.getFormattedContent(id);
   }
   
	@GetMapping("/navigation/{id}")
	@ResponseBody
	public List<Node> getContentNavigation(@PathVariable long id) {
	   return contentManager.getContentNavigation(id);
	}
	
}
