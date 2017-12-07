package hymn.bible.view.cion.dao;

 public class DB_Data {
	 public int _id;
	 public String title;
	 public int description;
	 
	public DB_Data(int _id, String title, int description) {
		this._id = _id;
		this.title = title;
		this.description = description;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDescription() {
		return description;
	}

	public void setDescription(int description) {
		this.description = description;
	}
 }
