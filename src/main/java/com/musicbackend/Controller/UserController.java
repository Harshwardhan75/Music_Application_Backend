package com.musicbackend.Controller;

import com.musicbackend.Dao.PlaylistRepository;
import com.musicbackend.Dao.SongRepository;
import com.musicbackend.Dao.UserRepository;
import com.musicbackend.Entity.Playlist;
import com.musicbackend.Entity.Song;
import com.musicbackend.Entity.User;
import com.musicbackend.Helper.PayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.File;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/")
    public ResponseEntity<?> userHome(Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            return ResponseEntity.ok().body(
                    new PayLoad("Welcome: " + user.getUsername(), "Success", LocalDateTime.now(), user)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Fetching Details Of User", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @PostMapping("/addSong")
    public ResponseEntity<?> addSong(@RequestParam String title, @RequestParam String artist,
                                     @RequestParam("file") MultipartFile file,
                                     Principal principal) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.ok(
                        new PayLoad("File is Empty", "Success", LocalDateTime.now(), null)
                );
            }

            String filename = file.getOriginalFilename();

            if (filename != null && filename.toLowerCase().endsWith(".mp3")) {
                //set upload directory
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                File upload = new File(uploadDir);
                if (!upload.exists()) {
                    upload.mkdir();
                }

                String filePath = uploadDir + filename;
                file.transferTo(new File(filePath));

                Song song = new Song();

                song.setPath(filePath);
                User user = this.userRepository.findByEmail(principal.getName());
                song.setTitle(title);
                song.setArtist(artist);
                song.setUploadDate(LocalDate.now());
                song.setUploadedBy(user);
                user.getSongs().add(song);
                this.userRepository.save(user);
                return ResponseEntity.ok(
                        new PayLoad("Song Added Successfully", "Success", LocalDateTime.now(), song)
                );
            } else {
                return ResponseEntity.ok(
                        new PayLoad("Enter A Valid Audio File", "Success", LocalDateTime.now(), null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error Occured While Uploading The File", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/mySong")
    public ResponseEntity<?> mySong(Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            List<Song> songs = new ArrayList<>(user.getSongs());
            if (songs.isEmpty()) {
                return ResponseEntity.ok(
                        new PayLoad("You have Not Uploaded Any Songs Yet", "Success",
                                LocalDateTime.now(), null)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("Songs Uploaded By You", "Success", LocalDateTime.now(), songs)
            );

        } catch (Exception e) {
            return ResponseEntity.ok(
                    new PayLoad("Error While Fetching Songs", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @PostMapping("/createPlaylist")
    public ResponseEntity<?> createPlaylist(@RequestBody Playlist playlist, Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            playlist.setCreatedBy(user);
            Playlist playlist1 = this.playlistRepository.save(playlist);
            return ResponseEntity.ok(
                    new PayLoad("Playlist Created Successfully","Success",LocalDateTime.now(),playlist1)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error Occurred While Creating The Playlist", "Failure",
                            LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/likedByMe")
    public ResponseEntity<?> likedByMe(Principal principal){
        try{

            User user = this.userRepository.findByEmail(principal.getName());
            List<Song> songs = this.songRepository.findLikedByUser(user);

            if(songs.isEmpty()){
                return ResponseEntity.ok(
                        new PayLoad("You Don't have Any Liked Songs","Success",LocalDateTime.now(),null)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("Songs Liked By You","Success",LocalDateTime.now(),songs)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Fetching Songs Liked By You","Failure",LocalDateTime.now(),null)
            );
        }
    }
}
