package com.gtphonehome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.gtphonehome.spring.cloud.aws.s3.S3ObjectView;
import com.gtphonehome.spring.cloud.aws.s3.SpringCloudS3Service;
import com.gtphonehome.storage.StorageFileNotFoundException;
import com.gtphonehome.storage.StorageProperties;
import com.gtphonehome.storage.StorageService;

@Controller
public class FileUploadController {

    private final StorageService storageService;
    
    private final SpringCloudS3Service s3Service;
    
    private final StorageProperties storageProperties;

    @Autowired
    public FileUploadController(StorageService storageService, 
    			SpringCloudS3Service s3Service,
    			StorageProperties storageProperties) {
        this.storageService = storageService;
        this.s3Service = s3Service;
        this.storageProperties = storageProperties;
    }
    
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
    		List<S3ObjectSummary> objects = this.s3Service.listObjects(this.storageProperties.getS3BucketName());
    	    List<S3ObjectView> objectsAndUrls = new ArrayList<S3ObjectView>(); 
    		
    		for (S3ObjectSummary s3Object: objects) {
    		    UriComponents uri = MvcUriComponentsBuilder.
	    				fromMethodName(FileUploadController.class, "serveFile", s3Object.getKey())
	    				.build();
    		    S3ObjectView s3ObjectView = new S3ObjectView();
    		    s3ObjectView.setUri(uri.toString());
    		    s3ObjectView.setKey(s3Object.getKey());
    		    s3ObjectView.setSize(s3Object.getSize());
    		    objectsAndUrls.add(s3ObjectView);
    		}
        model.addAttribute("files", objectsAndUrls);
    	   
        return "uploadForm";
    }
    
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
    	
    	    s3Service.downloadObject(this.storageProperties.getS3BucketName(), filename);
   	
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
    	
        storageService.store(file);
        
        Path filePath = storageService.load(file.getOriginalFilename());        
        if (filePath != null) {
        		File s3File = filePath.toFile();
        		s3Service.uploadObject(this.storageProperties.getS3BucketName(), s3File);	
        }
                
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
