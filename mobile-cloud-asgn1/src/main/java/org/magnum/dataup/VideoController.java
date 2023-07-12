/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.*;
import org.magnum.dataup.model.VideoStatus.VideoState;
@Controller
public class VideoController {

	private static final AtomicLong currentId = new AtomicLong(0L);
	
	private Map<Long,Video> videos = new HashMap<Long, Video>();
	private static VideoFileManager vfm;
	
	public VideoController() {
		super();
		try{vfm=VideoFileManager.get();}
		catch(IOException e){
			e.printStackTrace();
		};
	}
	public Video save(Video entity) {
		checkAndSetId(entity);
		videos.put(entity.getId(), entity);
		return entity;
	}

	private void checkAndSetId(Video entity) {
		if(entity.getId() == 0){
			entity.setId(currentId.incrementAndGet());
		}
	}
	private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

 	private String getUrlBaseForLocalServer() {
	   HttpServletRequest request = 
	       ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	   String base = 
	      "http://"+request.getServerName() 
	      + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
	   return base;
	}
	@RequestMapping(value="/video", method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(){
		return videos.values();
	}
  	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){
  		Video now=save(v);
  		now.setDataUrl(getDataUrl(now.getId()));
  		return now;
	}
  	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
  	public @ResponseBody VideoStatus setVideoData(@PathVariable("id") long id,
  			@RequestPart("data") MultipartFile videoData,
  			HttpServletResponse response)throws IOException{
  		if(videos.containsKey(id)==false){
  			response.setStatus( HttpServletResponse.SC_NOT_FOUND  );
  			return null;
  	    }

  		
  		try{
  			Video v=videos.get(id);
//  			v.setContentType(videoData.getContentType());
  			
  			InputStream in=videoData.getInputStream();
  			vfm.saveVideoData(v, in);
  			response.setStatus( HttpServletResponse.SC_OK );
  			
  		}catch(IOException e){
  			e.printStackTrace();
  		}
  		return new VideoStatus(VideoState.READY);
  	}

  	@RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
  	void getData(@PathVariable("id") long id,
    		HttpServletResponse response)throws IOException {
  		if(videos.containsKey(id)==false||vfm.hasVideoData(videos.get(id))==false){
  			response.setStatus( HttpServletResponse.SC_NOT_FOUND  );
  			return;
//  			return response;
//  			return new Response(getDataUrl(id),404,"dfsd",new ArrayList<Header>(),new TypedString(""));
  	    }
  		try {
  			Video v=videos.get(id);
//  			response.setContentType(v.getContentType());
  			
			OutputStream out = response.getOutputStream();
			vfm.copyVideoData(v,out );
			response.setStatus( HttpServletResponse.SC_OK );
		
  			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return;
//  		return response;
//  		return new Response(getDataUrl(id),status,"dfsd",new ArrayList<Header>(),new TypedString(""));
  	}
}
