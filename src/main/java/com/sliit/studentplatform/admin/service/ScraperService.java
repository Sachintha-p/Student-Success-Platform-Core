package com.sliit.studentplatform.admin.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class ScraperService {

    public String runJobScraper() {
        try {
            // Paths to your specific Python environment and script
            String pythonExe = "C:\\Users\\Sachintha Praneeth\\Desktop\\ITPM\\job-scraper\\.venv\\Scripts\\python.exe";
            String scriptPath = "C:\\Users\\Sachintha Praneeth\\Desktop\\ITPM\\job-scraper\\run_me.py";

            ProcessBuilder pb = new ProcessBuilder(pythonExe, scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("🐍 Python: " + line);
            }

            int exitCode = process.waitFor();
            return (exitCode == 0) ? "Successfully synced with TopJobs!" : "Scraper finished with errors.";

        } catch (Exception e) {
            return "Failed to trigger scraper: " + e.getMessage();
        }
    }
}