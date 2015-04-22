package com.wazzanau.terdoppio.metainfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wazzanau.terdoppio.bencode.BEDictionary;
import com.wazzanau.terdoppio.bencode.BEInteger;
import com.wazzanau.terdoppio.bencode.BEList;
import com.wazzanau.terdoppio.bencode.BEString;
import com.wazzanau.terdoppio.bencode.BEValue;
import com.wazzanau.terdoppio.bencode.BEncoding;
import com.wazzanau.terdoppio.bencode.DecodingException;

public class InfoDictionary {

	private final static String KEY_PIECE_LENGTH = "piece length";
	private final static String KEY_PIECES = "pieces";
	private final static String KEY_LENGTH = "length";
	private final static String KEY_PATH = "path";
	private final static String KEY_FILES = "files";
	private final static String KEY_NAME = "name";
	private final static String KEY_MD5_SUM = "md5sum";

	private String name;
	private Pieces pieces;
	private long pieceLength;
	private long length;
	private String md5sum;
	private List<FileItem> files;
	private boolean isSingleFileMode = false;
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pieces getPieces() {
		return pieces;
	}

	public void setPieces(Pieces pieces) {
		this.pieces = pieces;
	}

	public long getPieceLength() {
		return pieceLength;
	}

	public void setPieceLength(long pieceLength) {
		this.pieceLength = pieceLength;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public List<FileItem> getFiles() {
		return files;
	}

	public void setFiles(List<FileItem> files) {
		this.files = files;
	}

	public boolean isSingleFileMode() {
		return isSingleFileMode;
	}
	
	public boolean isMultiFileMode() {
		return !isSingleFileMode;
	}

	public void setSingleFileMode(boolean isSingleFileMode) {
		this.isSingleFileMode = isSingleFileMode;
	}
	
	public void setMultiFileMode(boolean isMultiFileMode) {
		this.isSingleFileMode = !isMultiFileMode;
	}
	
	/**
	 * Validates the given dictionary, for being a compliant metainfo dictionary.
	 * @param dict
	 * @return the validation result with the description of what went wrong if the dictionary is not valid.
	 */
	public static ValidationResult isValidInfoDictionary(BEDictionary dict) {
		if (!dict.containsKey(KEY_PIECES)) {
			return ValidationResult.falseBecauseOfMissingKey(KEY_PIECES);
		}
		
		if (!dict.containsKey(KEY_PIECE_LENGTH)) {
			return ValidationResult.falseBecauseOfMissingKey(KEY_PIECE_LENGTH);
		}
		
		if (!dict.containsKey(KEY_NAME)) {
			return ValidationResult.falseBecauseOfMissingKey(KEY_NAME);
		}

		boolean isSingleFileMode = (!dict.containsKey(KEY_FILES));
		
		if (isSingleFileMode) {
			/*
 			 * Info in Single File Mode
			 * For the case of the single-file mode, the info dictionary contains the following structure:
			 * 		name: the filename. This is purely advisory. (string)
    		 * 		length: length of the file in bytes (integer)
    		 * 		md5sum: (optional) a 32-character hexadecimal string corresponding to the MD5 sum of the file. This is not used by BitTorrent at all, but it is included by some programs for greater compatibility.
    		 */
			if (!dict.containsKey(KEY_LENGTH)) {
				ValidationResult.falseBecauseOfMissingKey(KEY_LENGTH);
			} 
			
		} else { 
			/*
			 * Info in Multiple File Mode
 			 * For the case of the multi-file mode, the info dictionary contains the following structure:
             * 		name: the file path of the directory in which to store all the files. This is purely advisory. (string)
			 * 		files: a list of dictionaries, one for each file. Each dictionary in this list contains the following keys:
    		 * 		length: length of the file in bytes (integer)
    		 * 		md5sum: (optional) a 32-character hexadecimal string corresponding to the MD5 sum of the file. This is not used by BitTorrent at all, but it is included by some programs for greater compatibility.
    		 * 		path: a list containing one or more string elements that together represent the path and filename. Each element in the list corresponds to either a directory name or (in the case of the final element) the filename. For example, a the file "dir1/dir2/file.ext" would consist of three string elements: "dir1", "dir2", and "file.ext". This is encoded as a bencoded list of strings such as l4:dir14:dir28:file.exte
			 */
			
			if (!dict.containsKey(KEY_FILES)) {
				return ValidationResult.falseBecauseOfMissingKey(KEY_FILES);
			} else {
				BEList filesList = dict.get(KEY_FILES).asList();
				for (BEValue item: filesList) {
					BEDictionary file = item.asDict();
					if (!file.containsKey(KEY_LENGTH)) {
						return ValidationResult.falseBecauseOfMissingKey(KEY_LENGTH);
					}
					
					if (!file.containsKey(KEY_PATH)) {
						return ValidationResult.falseBecauseOfMissingKey(KEY_PATH);
					}
				}
			}
			
		}
			
		return ValidationResult.VALID;
	}

	/**
	 * 
	 * @return the metainfo object encoded a BEDictionary, or null if the content of the metainfo object are not valid.
	 */
	public BEDictionary asDict() {
		BEDictionary info = new BEDictionary();
		info.put(KEY_NAME, new BEString(name));
		info.put(KEY_PIECES, pieces.asBEString());
		info.put(KEY_PIECE_LENGTH, new BEInteger(pieceLength));
		if (md5sum != null) {
			info.put(KEY_MD5_SUM, new BEString(md5sum));
		}
		
		if (isSingleFileMode) {
			info.put(KEY_LENGTH, new BEInteger(length));
		} else { // multi file mode
			BEList fileList = new BEList();
			for (FileItem item: files) {
				fileList.add(item.asDict());
			}
			info.put(KEY_FILES, fileList);
		}
		
		return isValidInfoDictionary(info).isValid() ? info : null;
	}
	
	/**
	 * Static factory method to get a InfoDictionary out of a BEncoded dictionary.
	 * @param dict
	 * @return
	 * @throws DecodingException If the given dictionary does not conform to the Info dictionary spec, decodi
	 */
	public static InfoDictionary fromDict(BEDictionary dict) throws DecodingException {
		ValidationResult validation = isValidInfoDictionary(dict);
		if (validation.isValid()) {
			InfoDictionary info = new InfoDictionary();
			info.pieces = BEncoding.decodePieces(dict.get(KEY_PIECES).asString());
			info.pieceLength = dict.get(KEY_PIECE_LENGTH).asInt().get();
			info.name = dict.get(KEY_NAME).asString().get();
			
			if (dict.containsKey(KEY_MD5_SUM)) { // md5sum is optional
				info.md5sum = dict.get(KEY_MD5_SUM).asString().get();
			} 
			
			info.isSingleFileMode = (!dict.containsKey(KEY_FILES));
			if (info.isSingleFileMode) {
				info.length = dict.get(KEY_LENGTH).asInt().get();
				
			} else { // multi file mode
				BEList filesList = dict.get(KEY_FILES).asList();
				info.files = new ArrayList<FileItem>();
				
				int i = 0; // we need to keep the index of the file item.
				for (BEValue item: filesList) {
					BEDictionary file = item.asDict();
					long length = file.get(KEY_LENGTH).asInt().get();
					
					// joining the path list into a single path string
					BEList pathElements = file.get(KEY_PATH).asList();
					Path path = joinListOfPaths(pathElements.asListOfStrings());
					
					// getting the optional md5sum
					String md5sum = null;
					if (file.containsKey(KEY_MD5_SUM)) { // md5sum is optional
						md5sum = file.get(KEY_MD5_SUM).asString().get();
					}
					
					FileItem fItem = new FileItem(i++, path, length, md5sum);
					info.files.add(fItem);
				} 
			}
			
			return info;
				
		} else {
			throw new DecodingException(validation.getErrorReason());
		}
		
	}
	
//	private static List<String> splitPath(Path path) {
//		List<String> pathItems = new ArrayList<String>();
//		for (Path p: path) {
//			pathItems.add(p.toString());
//		}
//		return pathItems;
//	}

	private static Path joinListOfPaths(List<String> paths) {
		if (paths.isEmpty()) {
			throw new IllegalStateException("Cannot join an empty path list");
		}
		Iterator<String> iter = paths.iterator();
		Path path = Paths.get(iter.next());
		while (iter.hasNext()) {
			path = path.resolve(Paths.get(iter.next()));
		}
		return path;
	}
	
	@Override
	public String toString() {
		return "InfoDictionary [name=" + name + ", pieces=" + pieces + ", pieceLength=" + pieceLength + ", length="
				+ length + ", md5sum=" + md5sum + ", files=" + files + ", isSingleFileMode=" + isSingleFileMode + "]";
	}

	public static class ValidationResult {
		private final boolean isValid;
		private final String errorReason;
		
		public ValidationResult(boolean isValid, String errorReason) {
			this.isValid = isValid;
			this.errorReason = errorReason;
		}

		public boolean isValid() {
			return isValid;
		}

		public String getErrorReason() {
			return errorReason;
		}
		
		public static final ValidationResult VALID = new ValidationResult(true, "The dictionary is a valid Metainfo dictionary");
		
		public static final ValidationResult falseBecauseOfMissingKey(String key) {
			return new ValidationResult(false, "Key [" + key + "] not found for info dictionary");
		}
	}
	
	public static class FileItem {
		private final int index;
		private final Path path;
		private final long length;
		private final String md5sum;
		
		public FileItem(int index, Path path, long length, String md5sum) {
			this.index = index;
			this.path = path;
			this.length = length;
			this.md5sum = md5sum;
		}
		
		public int getIndex() {
			return index;
		}

		public Path getPath() {
			return path;
		}

		public long getLength() {
			return length;
		}

		public String getMd5sum() {
			return md5sum;
		}
		
		public BEDictionary asDict() {
			BEDictionary dict = new BEDictionary();
			dict.put(KEY_LENGTH, new BEInteger(length));
			if (md5sum != null) { // optional
				dict.put(KEY_MD5_SUM, new BEString(md5sum));
			}
				
			BEList pathList = new BEList();
			for (Path p: path) {
				pathList.add(new BEString(p.toString()));
			}
			dict.put(KEY_PATH, pathList);
			return dict;
		}

		@Override
		public String toString() {
			return "FileItem [index=" + index + ", path=" + path + ", length=" + length + ", md5sum=" + md5sum + "]";
		}
	}
}

